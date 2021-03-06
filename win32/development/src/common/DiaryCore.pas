unit DiaryCore;

{ Data & Persistence }

interface

uses
  // ���������
  SysUtils,

  // ������
  BusinessObjects,
  DiaryDatabase,
  Bases,
  DiaryRoutines,
  DiaryDAO,
  DiaryLocalSource,
  DiaryWebSource,

  FoodBaseDAO,
  FoodBaseLocalDAO,
  FoodBaseWebDAO,

  DishBaseDAO,
  DishBaseLocalDAO,
  DishBaseWebDAO,

  {#}DiaryWeb,  
  AnalyzeInterface,
  DiaryAnalyze,
  AutoLog,
  SettingsINI,

  // ������
  ThreadExecutor,
  InetDownload;

type
  TUpdateCheckResult = (urNoConnection, urNoUpdates, urCanUpdate);
  TItemType = (itUnknown, itFood, itDish);
  TSyncResult = (srEqual, srFirstUpdated, srSecondUpdated);

  procedure Initialize();
  procedure Finalize();

  { load/save }
  procedure LoadExpander; deprecated;
  procedure SaveExpander; deprecated;

  function IdentifyItem(const ItemName: string; out Item: TMutableItem): TItemType;

  { web }
  function DownloadFoodBaseSample: boolean;
  function DownloadDishBaseSample: boolean;
  function CheckUpdates(var LatestVersion: string): TUpdateCheckResult;

  function ExportKoofs(Plain: boolean): string;

var
  { ������ }
  LocalSource: TDiaryDAO;
  WebSource: TDiaryDAO;

  Diary: TDiary;

  WebClient: TDiacompClient;
  Expander: TStringMap;

  FoodBaseLocal: TFoodBaseDAO;
  FoodBaseWeb: TFoodBaseDAO;

  DishBaseLocal: TDishBaseDAO;
  DishBaseWeb: TDishBaseDAO;

  { ��������� }
  Inited: boolean = False;
  WORK_FOLDER: string = ''; // ������� ����������
  AnExpDate: string; { ��� �������� ���� ����������, ����������� ��� ������ }

const
  { ��������� }
  ADVANCED_MODE           = True;
  PROGRAM_VERSION         = '2.05';
  PROGRAM_VERSION_CODE    : integer = 205;
  PROGRAM_DATE            = '2015.03.26';
  UPDATES_CHECKING_PERIOD = 1; { ���� }

  { ����� ������������ ������ � �������� }
  BLOOD_ACTUALITY_TIME  = 120;     // minutes
  INS_ACTUALITY_TIME    = 4 * 60;  // minutes
  SEARCH_INTERVAL       = 14;      // for time left

  { ��������� ����������� }
  DOWNLOAD_APP_NAME   = 'DiaryCore';
  CONNECTION_TIME_OUT = 10000;
  MAX_FOODBASE_SIZE   = 500 * 1024; // byte
  MAX_DISHBASE_SIZE   = 500 * 1024; // byte

  { URL's }
  URL_SERVER          = 'http://diacomp.net/api/windows/';
  //URL_SERVER          = 'http://localhost:8190/api/windows/';

  URL_VERINFO         = URL_SERVER + 'version';
  URL_UPDATE          = URL_SERVER + 'file/compensation.exe';
  URL_MATHAN          = URL_SERVER + 'file/mathan.dll';
  URL_RESTART         = URL_SERVER + 'file/restart.exe';
  URL_FOODBASE_SAMPLE = URL_SERVER + 'file/demofoodbase.xml';
  URL_DISHBASE_SAMPLE = URL_SERVER + 'file/demodishbase.xml';

  { Local files }

  ANALYZE_LIB_FileName = 'MathAn.dll';

  // TODO: refactor constants' names

  FOLDER_BASES        = 'Bases';

    FoodBase_Name     = 'FoodBase.xml';
    FoodBase_Name_old = 'FoodBase.txt';
    FoodBaseHash_Name = 'FoodBaseHash.txt';

    DishBase_Name     = 'DishBase.xml';
    DishBase_Name_old = 'DishBase.txt';
    DishBaseHash_Name = 'DishBaseHash.txt';

    Diary_Name        = 'Diary.txt';

    Expander_Name     = 'Expander.txt';

    FoodBase_FileName     = FOLDER_BASES + '\' + FoodBase_Name;
    FoodBaseHash_FileName = FOLDER_BASES + '\' + FoodBaseHash_Name;

    DishBase_FileName     = FOLDER_BASES + '\' + DishBase_Name;
    DishBaseHash_FileName = FOLDER_BASES + '\' + DishBaseHash_Name;

    Diary_FileName        = FOLDER_BASES + '\' + Diary_Name;

    Expander_FileName     = FOLDER_BASES + '\' + Expander_Name;

implementation

{======================================================================================================================}
procedure Initialize();
{======================================================================================================================}
begin
  AutoLog.Log(DEBUG, 'Loading local diary...');
  LocalSource := TDiaryLocalSource.Create(WORK_FOLDER + Diary_FileName);
  AutoLog.Log(DEBUG, 'Local diary loaded');

  WebClient := TDiacompClient.Create;
  WebSource := TDiaryWebSource.Create(WebClient);

  Diary := TDiary.Create(LocalSource);

  FoodBaseLocal := TFoodBaseLocalDAO.Create(WORK_FOLDER + FoodBase_FileName);
  FoodBaseWeb := TFoodbaseWebDAO.Create(WebClient);

  DishBaseLocal := TDishBaseLocalDAO.Create(WORK_FOLDER + DishBase_FileName);
  DishBaseWeb := TDishbaseWebDAO.Create(WebClient);

  Expander := TStringMap.Create;
end;

{======================================================================================================================}
procedure Finalize();
{======================================================================================================================}
begin
  Diary.Free; // before sources finalization
  LocalSource.Free;
  WebSource.Free; // before WebClient
  WebClient.Free;

  FoodBaseLocal.Free;
  FoodBaseWeb.Free;
  DishBaseLocal.Free;
  DishBaseWeb.Free;


  Expander.Free;
end;

{======================================================================================================================}
function DownloadDishBaseSample(): boolean;
{======================================================================================================================}
begin
  Result := GetInetFile(
    DOWNLOAD_APP_NAME, URL_DISHBASE_SAMPLE,
    WORK_FOLDER + DishBase_FileName, nil, CONNECTION_TIME_OUT, MAX_DISHBASE_SIZE);
end;

{======================================================================================================================}
function DownloadFoodBaseSample(): boolean;
{======================================================================================================================}
begin
  Result := GetInetFile(
    DOWNLOAD_APP_NAME, URL_FOODBASE_SAMPLE,
    WORK_FOLDER + FoodBase_FileName, nil, CONNECTION_TIME_OUT, MAX_FOODBASE_SIZE);
end;

{======================================================================================================================}
function CheckUpdates(var LatestVersion: string): TUpdateCheckResult;
{======================================================================================================================}
const
  TEMP = 'TEMP.TXT';
var
  f: TextFile;
begin
  Log(DEBUG, 'Checking for app updates...');

  Value['LastUpdateCheck'] := Round(GetTimeUTC());
  Result := urNoConnection;
  try
    if GetInetFile('Compensation', URL_VERINFO, TEMP, nil, CONNECTION_TIME_OUT, 1024) then
    begin
      Log(DEBUG, 'Server responded OK, fetching response...');

      AssignFile(f, TEMP);
      Reset(f);
      Readln(f, LatestVersion);
      CloseFile(f);
      DeleteFile(TEMP);

      Log(DEBUG, 'Server response: ' + LatestVersion);

      if (StrToInt(LatestVersion) > PROGRAM_VERSION_CODE) then
      begin
        Log(DEBUG, 'New update available, version ' + LatestVersion);
        Result := urCanUpdate;
      end else
      begin
        Log(DEBUG, 'App is up-to-date');
        Result := urNoUpdates;
      end;
    end else
    begin
      Log(ERROR, 'Failed to request ' + URL_VERINFO);
    end;
  except
    on e: Exception do
    begin
      Log(ERROR, 'Checking failed: ' + e.Message);
    end;
  end;
end;

{======================================================================================================================}
function IdentifyItem(const ItemName: string; out Item: TMutableItem): TItemType;
{======================================================================================================================}
begin
  Item := FoodBaseLocal.FindOne(ItemName);
  if (Item <> nil) then
  begin
    Result := itFood;
    Exit;
  end;

  Item := DishBaseLocal.FindOne(ItemName);
  if (Item <> nil) then
  begin
    Result := itDish;
    Exit;
  end;

  Result := itUnknown;
end;

{======================================================================================================================}
procedure LoadExpander;
{======================================================================================================================}
begin
  if FileExists(WORK_FOLDER + Expander_FileName) then
    Expander.LoadFromFile(WORK_FOLDER + Expander_FileName);
end;

{======================================================================================================================}
procedure SaveExpander;
{======================================================================================================================}
begin
  Expander.SaveToFile(WORK_FOLDER + Expander_FileName);
end;

{======================================================================================================================}
function ExportKoofs(Plain: boolean): string;
{======================================================================================================================}
var
  i: integer;
  s: string;
  DC: char;
  Koof: TKoof;
begin
  StartProc('ExportKoofs()');

  Result := '';
  if (Plain) then
  begin
    for i := 0 to MinPerDay - 1 do
    begin
      Koof := GetKoof(i);
      Result := Result +
        //Format('%2.2d',[i]) + '.00 - '+Format('%2.2d',[i + 1]) + '.00' + #9+
        Format('%.6f'#9'%.6f'#9'%.6f'#13#10, [Koof.k, Koof.q, Koof.p])
      ;
    end;
  end else
  begin
    Result := '[';
    DC := DecimalSeparator;
    try                    
      DecimalSeparator := '.';

      for i := 0 to MinPerDay - 1 do
      begin
        Koof := GetKoof(i);

        s := Format('{"time":%d,"k":%.4f,"q":%.2f,"p":%.2f}',[i, Koof.k, Koof.q, Koof.p]);
        if (i < MinPerDay - 1) then
          s := s + ',';
        Result := Result + s;
      end;
    finally
      DecimalSeparator := DC;
    end;  
    Result := Result + ']';
  end;

  FinishProc;
end;

end.
