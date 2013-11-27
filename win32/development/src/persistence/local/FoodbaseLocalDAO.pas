unit FoodbaseLocalDAO;

interface

uses
  SysUtils,
  BusinessObjects,
  FoodbaseDAO,
  Bases;

type
  TFoodbaseLocalDAO = class (TFoodbaseDAO)
  private
    FFileName: string;
    FBase: TFoodBase;
  private
    function GetIndex(Food: TFood): integer;
  public
    constructor Create(const FileName: string);
    destructor Destroy; override;

    procedure Add(Food: TFood); override;
    procedure Delete(Food: TFood); override;
    function FindAll(): TFoodList; override;
    function FindAny(const Filter: string): TFoodList; override;
    function FindOne(const Name: string): TFood; override;
    procedure ReplaceAll(const NewList: TFoodList; NewVersion: integer); override;
    procedure Update(Food: TFood); override;
    function Version(): integer; override;
  end;

implementation

{ TFoodbaseLocalDAO }

{==============================================================================}
procedure TFoodbaseLocalDAO.Add(Food: TFood);
{==============================================================================}
var
  Index: integer;
  Temp: TFood;
begin
  Index := GetIndex(Food);
  if (Index = -1) then
  begin
    Temp := TFood.Create;
    Temp.CopyFrom(Food);
    FBase.Add(Temp);
    FBase.SaveToFile(FFileName);
  end else
    raise EDuplicateException.Create(Food);
end;

{==============================================================================}
constructor TFoodbaseLocalDAO.Create(const FileName: string);
{==============================================================================}
begin
  FBase := TFoodBase.Create;
  if FileExists(FileName) then
    FBase.LoadFromFile_XML(FileName);
  FFileName := FileName;
end;

{==============================================================================}
procedure TFoodbaseLocalDAO.Delete(Food: TFood);
{==============================================================================}
var
  Index: integer;
begin
  Index := GetIndex(Food);
  if (Index > -1) then
  begin
    FBase.Delete(Index);
    FBase.SaveToFile(FFileName);
  end else
    raise EItemNotFoundException.Create(Food);
end;

{==============================================================================}
destructor TFoodbaseLocalDAO.Destroy;
{==============================================================================}
begin
  FBase.Free;
  inherited;
end;

{==============================================================================}
function TFoodbaseLocalDAO.FindAll(): TFoodList;
{==============================================================================}
var
  i: integer;
begin
  SetLength(Result, FBase.Count);
  for i := 0 to FBase.Count - 1 do
  begin
    Result[i] := TFood.Create;
    Result[i].CopyFrom(FBase[i]);
  end;
end;

{==============================================================================}
function TFoodbaseLocalDAO.FindAny(const Filter: string): TFoodList;
{==============================================================================}
var
  i, k: integer;
begin
  SetLength(Result, FBase.Count);
  k := 0;
  for i := 0 to FBase.Count - 1 do
  // TODO: optimize
  if (pos(AnsiUpperCase(Filter), AnsiUpperCase(FBase[i].Name)) > 0) then
  begin
    inc(k);
    SetLength(Result, k);
    Result[k - 1] := TFood.Create;
    Result[k - 1].CopyFrom(FBase[i]);
  end;
  SetLength(Result, k);
end;

{==============================================================================}
function TFoodbaseLocalDAO.FindOne(const Name: string): TFood;
{==============================================================================}
var
  Index: integer;
begin
  Index := FBase.Find(Name);
  if (Index <> -1) then
  begin
    Result := TFood.Create;
    Result.CopyFrom(FBase[Index]);
  end else
    Result := nil;
end;

{==============================================================================}
function TFoodbaseLocalDAO.GetIndex(Food: TFood): integer;
{==============================================================================}
begin
  Result := FBase.GetIndex(Food.ID);
end;

{==============================================================================}
procedure TFoodbaseLocalDAO.ReplaceAll(const NewList: TFoodList;
  NewVersion: integer);
{==============================================================================}
var
  i: integer;
  Food: TFood;
begin
  FBase.Clear;
  for i := 0 to High(NewList) do
  begin
    Food := TFood.Create;
    Food.CopyFrom(NewList[i]);
    FBase.Add(Food);
  end;
  FBase.Version := NewVersion;
  FBase.SaveToFile(FFileName);
end;

{==============================================================================}
procedure TFoodbaseLocalDAO.Update(Food: TFood);
{==============================================================================}
var
  Index: integer;
begin
  Index := GetIndex(Food);
  if (Index <> -1) then
  begin
    FBase[Index].CopyFrom(Food);
    FBase.Sort;
    FBase.SaveToFile(FFileName);
  end else
    raise EItemNotFoundException.Create(Food);
end;

{==============================================================================}
function TFoodbaseLocalDAO.Version: integer;
{==============================================================================}
begin
  Result := FBase.Version;
end;

end.