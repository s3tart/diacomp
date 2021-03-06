unit AnalyzeGraphic;

interface

uses
  AnalyzeInterface,
  DiaryRoutines,
  Windows,
  ExtCtrls,
  Classes,
  SysUtils,
  Graphics,
  Math,
  AutoLog,
  DiaryPage,
  DiaryDatabase,
  DiaryRecords,
  BusinessObjects;

type
  // TODO: remove
  TKoofType = (kfK, kfQ, kfP, kfX);

  TDayCurve = array[0..MinPerDay - 1] of Real;
  TWeightedDayPoint = record
    Time: 0..MinPerDay - 1;
    Value: Real;
    Weight: Real;
  end;

  procedure PrepareBS(Image: TImage; Max: real; Mini: boolean;
    var kx,ky: real; var Border: integer);
  procedure DrawBS(Recs: TRecordList; BaseDate: TDateTime; Image: TImage; Mini: boolean);
  procedure DrawBS_Int(const Base: TDiary; FromDay, ToDay: integer; Image: TImage);

  procedure DrawKoof(Image: TImage; const KoofList: TKoofList;
    const RecList: TAnalyzeRecList; KoofType: TKoofType; DrawPoints: boolean);
  procedure DrawDayCurve(Image: TImage; const Curve: TDayCurve;
    const Points: array of TWeightedDayPoint; ColorCurve, ColorPointWeight0,
    ColorPointWeight1: TColor);

var
  LeftBord: integer;
  
const
  Brd      = 20; { BS    }
  eSize    = 3;  { ...   }
  TopBord  = 20; { Koofs }

  COLOR_BS_HIGH   = $E6E6FF; // $000060;
  COLOR_BS_NORMAL = $E6FFE6; // $006000;
  COLOR_BS_CURVE  = clMaroon; // $FFFFFF;
  COLOR_K         = $EEEEFF;
  COLOR_Q         = $FFEEEE;
  COLOR_P         = $80FFFF;
  COLOR_X         = $EEEEEE;
  COLOR_BACK      = clWhite; // clBlack
  COLOR_TIMEPOS   = clSilver;
  COLOR_AXIS_MAIN = clBlack; // clYellow;
  COLOR_AXIS_SUB  = $DCDCDC;
  COLOR_TITLES    = clBlack; // clLime

implementation

uses SettingsINI;

{======================================================================================================================}
procedure PrepareBS(Image: TImage; Max: real; Mini: boolean;
  var kx, ky: real; var Border: integer);
{======================================================================================================================}
var
  w, h: integer;
  i: integer;
  x1, y1, x2, y2: integer;

  BS_PREPRAND_LOW: Real;
  BS_PREPRAND_HIGH: Real;
  BS_POSTPRAND_HIGH: Real;

  procedure CalcXY(Time: integer; Value: real; var x, y: integer);
  begin
    x := Round(Border + kx / MinPerHour * Time);
    y := Round(h - Border - ky * Value);
  end;

begin
  BS_PREPRAND_LOW   := Value['BS1'];
  BS_PREPRAND_HIGH  := Value['BS2'];
  BS_POSTPRAND_HIGH := Value['BS3'];

  with Image.Canvas do
  begin
    w := Image.Width;
    h := Image.Height;
    Image.Picture.Bitmap.Width := w;
    Image.Picture.Bitmap.Height := h;

    { ������� }
    Brush.Color := COLOR_BACK;
    FillRect(Image.ClientRect);

    {if Mini then
      Border := Round(1.00*BRD)
    else
      Border := BRD; }
    Border := 3 * TextHeight('123') div 2;
    ky := (h - 2 * Border) / Max;
    kx := (w - 2 * Border) / HourPerDay;

    { ������ ���� }
    Brush.Color := COLOR_BS_NORMAL;
    CalcXY(0, BS_PREPRAND_LOW, x1, y1);
    CalcXY(MinPerDay, BS_PREPRAND_HIGH, x2, y2);
    FillRect(Rect(x1, y1, x2, y2));

    { ������� ���� }
    Brush.Color := COLOR_BS_HIGH;
    CalcXY(0, BS_PREPRAND_HIGH, x1, y1);
    CalcXY(MinPerDay, BS_POSTPRAND_HIGH, x2, y2);
    FillRect(Rect(x1, y1, x2, y2));

    { ����� }
    Brush.Color := COLOR_BACK;
    Pen.Color := COLOR_AXIS_SUB;
    Pen.Width := 1;
    Font.Style := [];
    Font.Color := COLOR_TITLES;

    if Mini then
    begin
      Pen.Style := psSolid;
      Font.Size := 5;
    end else
    begin
      Pen.Style := psDot;
      Font.Size := 8;
    end;

    { ����� Y }
    for i := 1 to Round(Max) do
    begin
      MoveTo(Border - 3, Round(h - Border - ky * i));
      LineTo(w - Border, Round(h - Border - ky * i));
      if (not mini) or ((i mod 2) = 0) then
        TextOut(
          Border - 15,
          h - Border - Round(ky * i) - (TextHeight('0') div 2),
          IntToStr(i)
        );
    end;

    { ����� X }
    for i := 0 to HourPerDay do
    if (not mini) or ((i mod 3) = 0) then
    begin
      MoveTo(Border + Round(i * kx), Border);
      LineTo(Border + Round(i * kx), h - Border + 2);
      TextOut(
        Border + Round(i * kx) - (TextWidth(IntToStr(i)) div 2),
        h - Border + 4,
        IntToStr(i)
      );
    end;

    { ��� }
    Pen.Style := psSolid;
    Pen.Color := COLOR_AXIS_MAIN;
    MoveTo(Border,Border);
    LineTo(Border, h - Border);
    LineTo(w - Border, h - Border);
  end;
end;

{======================================================================================================================}
procedure DrawBS(Recs: TRecordList; BaseDate: TDateTime; Image: TImage; Mini: boolean);
{======================================================================================================================}

  function FindMax: real;
  const
    InitMax = 8;
  var
    i: integer;
  begin
    Result := InitMax;

    for i := 0 to High(Recs) do
    if (Recs[i].RecType = TBloodRecord) and
       (TBloodRecord(Recs[i]).Value > Result) then
      Result := TBloodRecord(Recs[i]).Value;
  end;

  function FindFirstBS(): integer;
  var
    i: integer;
  begin
    for i := Low(Recs) to High(Recs) do
    if (recs[i].RecType = TBloodRecord) then
    begin
      Result := i;
      Exit;
    end;
    Result := -1;
  end;

var
  h: integer;
  kx, ky: real;
  max: real;
  i, k: integer;
  cx, cy: integer;
  TempBlood: TBloodRecord;
  Border: integer;

  procedure CalcXY(Time: TDateTime; Value: real; var x, y: integer);
  begin
    x := Round(Border + kx * (Time - BaseDate) * HourPerDay);
    y := Round(h - Border - ky * Value);
  end;

begin
  max := FindMax();
  PrepareBS(Image, Max, Mini, kx, ky, Border);
  Image.Picture.Bitmap.Width := Image.Width;
  Image.Picture.Bitmap.Height := Image.Height;

  k := FindFirstBS();
  if (k = -1) then
  begin
    Exit;
  end;

  h := Image.Height;

  with Image.Canvas do
  begin
    { ������ }
    Pen.Width := 1;
    if Mini then
      Pen.Style := psSolid
    else
      Pen.Style := psDot;

    Pen.Color := COLOR_BS_CURVE;

    { ������ ����� }

    TempBlood := TBloodRecord(Recs[k]);
    CalcXY(TempBlood.Time, TempBlood.Value, cx, cy);
    MoveTo(cx, cy);

    { �������� ����� }
    for i := k + 1 to High(Recs) do
    if (Recs[i].RecType = TBloodRecord) then
    begin
      TempBlood := TBloodRecord(Recs[i]);
      CalcXY(TempBlood.Time, TempBlood.Value, cx, cy);
      LineTo(cx, cy);
    end;

    { ����� }
    if (not mini) then
    begin   
      Pen.Color := COLOR_BS_CURVE;
      Pen.Style := psSolid;
      for i := Low(Recs) to High(Recs) do
      if (Recs[i].RecType = TBloodRecord) then
      begin
        TempBlood := TBloodRecord(Recs[i]);
        CalcXY(TempBlood.Time, TempBlood.Value, cx, cy);

        if (TempBlood.PostPrand) then
          Brush.Color := COLOR_BACK
        else
          Brush.Color := COLOR_BS_CURVE;

        Ellipse(cx - eSize, cy - eSize, cx + eSize, cy + eSize);
      end;
    end;
  end;
end;

{======================================================================================================================}
procedure DrawBS_Int(const Base: TDiary; FromDay, ToDay: integer; Image: TImage);
{======================================================================================================================}
(*const
  BRD   = 20;
  eSize = 3;
  InitMax = 8;
var
  h: integer;
  kx,ky: real;
  max: real;
  //x1,y1,x2,y2: integer;
  cx,cy: integer;
  //TempBlood: TBloodRecord;

  points: array of
  record
    R: array of
    record
      Pos: real;
      Value: real;
    end;
    AvqPos: real;
    AvqValue: real;
  end;
  n,i: integer;
  Zone: integer;
  Border: integer;

  procedure CalcXY(Time,Value: real; var x,y: integer); overload;
  begin
    x := Round(Border + kx/60*Time);
    y := Round(h-Border - ky*Value);
  end;   *)

begin
 (* { �������� ������������ FromDay � ToDay }
  if (FromDay>=0)and(FromDay<Base.Count)and
     (ToDay>=0)and(ToDay<Base.Count)
  then
  begin
    { �������� ������� ���������� }
    if FromDay>ToDay then
    begin
      FromDay := FromDay xor ToDay;
      ToDay   := FromDay xor ToDay;
      FromDay := FromDay xor ToDay;
    end;

    { ���������� ����� }
    SetLength(points,24);
    for n := FromDay to ToDay do
    for i := 0 to Base[n].Count-1 do
    if Base[n][i].TagType=rtBlood then
    begin
      Zone := (Base[n][i].Time div 60) mod 24;
      SetLength(Points[Zone].R,length(Points[Zone].R) + 1);
      Points[Zone].R[High(Points[Zone].R)].Value := TBloodRecord(Base[n][i]).Value;
      Points[Zone].R[High(Points[Zone].R)].Pos := Frac(Base[n][i].Time/60);
    end;

    { ���������� ������� � ��������� }
    Max := InitMax;
    for i := 0 to 23 do
    begin
      Points[i].AvqPos := 0;
      Points[i].AvqValue := 0;
      if length(Points[i].R)>0 then
      begin
        for n := 0 to High(Points[i].R) do
        begin
          Points[i].AvqPos := Points[i].AvqPos + Points[i].R[n].Pos;
          Points[i].AvqValue := Points[i].AvqValue + Points[i].R[n].Value;
        end;
        Points[i].AvqPos := Points[i].AvqPos / length(Points[i].R);
        Points[i].AvqValue := Points[i].AvqValue / length(Points[i].R);
      end;

      if Points[i].AvqValue > Max then
        Max := Points[i].AvqValue;
    end;

    { ������� }
    PrepareBS(Image,Max,false,kx,ky,Border);

    { ��������� }
    {w := Image.Width; }
    h := Image.Height;
    with Image.Canvas do
    begin
      CalcXY(
        Points[0].AvqPos*60,
        Points[0].AvqValue,
        cx,cy);
      MoveTo(cx,cy);

      for i := 0 to 23 do
      if Points[i].AvqValue>0 then
      begin
        CalcXY(
          (i+Points[i].AvqPos)*60,
          Points[i].AvqValue,
          cx,cy);
        LineTo(cx,cy);
      end;
    end;
  end else
  begin
    { ������� }
    PrepareBS(Image,InitMax,false,kx,ky,Border);
  end;
         *)



(*  max := FindMax;
  w := Image.Width;
  h := Image.Height;
  ky := (h-2*Border)/max;
  kx := (w-2*Border)/24;

  with Image.Canvas do
  begin
    { ������� }
    Brush.Color := clWhite;
    FillRect(Image.ClientRect);

    Pen.Color := clBlack;
    Pen.Width := 1;
    Font.Style := [];
    Font.Color := clBlack;

    if Mini then
    begin
      Font.Size := 5;
      Pen.Style := psSolid;
    end else
    begin
      Font.Size := 8;
      Pen.Style := psDot;
    end;

    { ������� ���� }
    Brush.Color := RGB(220,255,220);
    CalcXY(0,3.7,x1,y1);
    CalcXY(MinPerDay,6.2,x2,y2);
    FillRect(Rect(x1,y1,x2,y2));

    { ������� ���� }
    Brush.Color := RGB(255,220,220);
    CalcXY(0,6.2,x1,y1);
    CalcXY(MinPerDay,7.8,x2,y2);
    FillRect(Rect(x1,y1,x2,y2));

    Brush.Color := clWhite;

    { ����� Y }
    Pen.Color := RGB(220,220,220);
    for i := 1 to Round(max) do
    begin
      MoveTo(Border-3,Round(h-Border-ky*i));
      LineTo(w-Border,Round(h-Border-ky*i));
      if (not mini)or
         ((i mod 2)=0) then
      TextOut(
        Border-15,
        h-Border-Round(ky*i)-(TextHeight('0') div 2),
        IntToStr(i)
      );
    end;

    { ����� X }
    for i := 0 to 24 do
    if (not mini)or
       ((i mod 3)=0) then
    begin
      MoveTo(Border+Round(i*kx),Border);
      LineTo(Border+Round(i*kx),h-Border+2);
      TextOut(
        Border+Round(i*kx)-(TextWidth('0') div 2),
        h-Border+4,
        IntToStr(i)
      );
    end;

    { ��� }
    Pen.Style := psSolid;
    Pen.Color := clBlack;
    MoveTo(Border,Border);
    LineTo(Border,h-Border);
    LineTo(w-Border,h-Border);

    {--------------------------}
    if (Day<0)or(Day>=Base.Count) then Exit;
    {--------------------------}

    { ������ }
    Pen.Width := 1;
    if Mini then
      Pen.Style := psSolid
    else
      Pen.Style := psDot;

    Pen.Color := clMaroon;

    { ��������� ����� ����������� ��� }
    if (Day>0) then
    begin
      TempBlood := Base[day-1].LastBloodRec;
      if TempBlood<>nil then
      begin
        CalcXY(
          TempBlood.Time-MinPerDay,
          TempBlood.Value,
          cx,cy);
        MoveTo(cx,cy);
      end;
    end else
    begin
      TempBlood := Base[day].FirstBloodRec;
      if TempBlood<>nil then
      begin
        CalcXY(
          TempBlood.Time,
          TempBlood.Value,
          cx,cy);
        MoveTo(cx,cy);
      end;
    end;

    { �������� ����� }
    for i := 0 to Base[Day].Count-1 do
    if Base[Day][i].TagType=rtBlood then
    begin
      CalcXY(
        TBloodRecord(Base[Day][i]).Time,
        TBloodRecord(Base[Day][i]).Value,
        cx,cy);
      LineTo(cx,cy);
    end;

    { ����������� �� ��������� ���� }

    if (Day<Base.Count-1)then
    begin
      TempBlood := Base[Day + 1].FirstBloodRec;
      if TempBlood<>nil then
      begin
        CalcXY(
          TempBlood.Time+MinPerDay,
          TempBlood.Value,
         cx,cy);
        LineTo(cx,cy);
      end;
    end;

    { ����� }
    if not mini then
    begin
      Brush.Color := clMaroon;
      Pen.Color := clMaroon;
      Pen.Style := psSolid;
      for i := 0 to Base[Day].Count-1 do
      if Base[Day][i].TagType=rtBlood then
      begin
        CalcXY(
          TBloodRecord(Base[Day][i]).Time,
          TBloodRecord(Base[Day][i]).Value,
          cx,cy);
        Ellipse(cx-eSize,cy-eSize,cx+eSize,cy+eSize);
      end;
    end;
  end;   *)
end;

{======================================================================================================================}
procedure DrawKoof(Image: TImage; const KoofList: TKoofList;
  const RecList: TAnalyzeRecList; KoofType: TKoofType; DrawPoints: boolean);
{======================================================================================================================}

  function GetK(const Rec: TAnalyzeRec): real;
  begin
    if Rec.Carbs > 0 then
      Result := (
        - KoofList[Rec.Time].p * Rec.Prots
        + KoofList[Rec.Time].q * Rec.Ins
        + (Rec.BSOut - Rec.BSIn)
      ) / Rec.Carbs
    else
      Result := -10;
  end;

  function GetX(k, q, p: real): real; overload;
  const
    PROTS = 0.25;
  begin
    if abs(q) > 0.0001 then
      Result := (k + PROTS * p) / q
    else
      Result := 0.0;
  end;

  function GetX(n: integer): real; overload;
  begin
    Result := GetX (KoofList[n].k, KoofList[n].q, KoofList[n].p);
  end;

  function GetX(const Rec: TAnalyzeRec): real; overload;
  begin
    Result := GetX(Rec.Time);
  end;

  function GetQ(const Rec: TAnalyzeRec): real;
  begin
    if Rec.Ins > 0 then
      Result := (
        + KoofList[Rec.Time].p * Rec.Prots
        + KoofList[Rec.Time].k * Rec.Carbs
        - (Rec.BSOut-Rec.BSIn)
      ) / (Rec.Ins)
    else
      Result := -10;
  end;

  function GetP(const Rec: TAnalyzeRec): real;
  begin
    if Rec.Prots > 0 then
      Result := (
        - KoofList[Rec.Time].k * Rec.Carbs
        + KoofList[Rec.Time].q * Rec.Ins
        + (Rec.BSOut-Rec.BSIn)
      ) / (Rec.Prots)
    else
      Result := -10;
  end;

var
  Curve: TDayCurve;
  Points: array of TWeightedDayPoint;
  ColorCurve: TColor;
  ColorPointWeight0: TColor;
  ColorPointWeight1: TColor;
  i: integer;
begin
  StartProc('DrawKoof');

  // ������ ����� ������
  case KoofType of
    kfK:
      begin
        for i := 0 to MinPerDay - 1 do Curve[i] := KoofList[i].k;

        if (DrawPoints) then
        begin
          SetLength(Points, Length(RecList));
          for i := 0 to High(Points) do
          begin
            Points[i].Time := RecList[i].Time;
            Points[i].Value := GetK(RecList[i]);
            Points[i].Weight := RecList[i].Weight;
          end;
        end;

        ColorCurve := COLOR_K;
        ColorPointWeight0 := COLOR_BACK;
        ColorPointWeight1 := clRed;
      end;

    kfQ:
      begin
        for i := 0 to MinPerDay - 1 do Curve[i] := KoofList[i].q;

        if (DrawPoints) then
        begin
          SetLength(Points, Length(RecList));
          for i := 0 to High(Points) do
          begin
            Points[i].Time := RecList[i].Time;
            Points[i].Value := GetQ(RecList[i]);
            Points[i].Weight := RecList[i].Weight;
          end;
        end;

        ColorCurve := COLOR_Q;
        ColorPointWeight0 := COLOR_BACK;
        ColorPointWeight1 := clBlue;
      end;

    kfP:
      begin
        for i := 0 to MinPerDay - 1 do Curve[i] := KoofList[i].p;

        if (DrawPoints) then
        begin
          SetLength(Points, Length(RecList));
          for i := 0 to High(Points) do
          begin
            Points[i].Time := RecList[i].Time;
            Points[i].Value := GetP(RecList[i]);
            Points[i].Weight := RecList[i].Weight;
          end;
        end;

        ColorCurve := COLOR_P;
        ColorPointWeight0 := COLOR_BACK;
        ColorPointWeight1 := clOlive;
      end;

    kfX:
      begin
        for i := 0 to MinPerDay - 1 do Curve[i] := GetX(i);

        if (DrawPoints) then
        begin
          SetLength(Points, Length(RecList));
          for i := 0 to High(Points) do
          begin
            Points[i].Time := RecList[i].Time;
            Points[i].Value := GetX(RecList[i]);
            Points[i].Weight := RecList[i].Weight;
          end;
        end;

        ColorCurve := COLOR_X;
        ColorPointWeight0 := COLOR_BACK;
        ColorPointWeight1 := clBlack;
      end;

    else
      raise Exception.Create('����������� ��� ������������');
  end;

  // ������
  DrawDayCurve(Image, Curve, Points, ColorCurve, ColorPointWeight0, ColorPointWeight1);

  FinishProc;
end;

{======================================================================================================================}
procedure DrawDayCurve(Image: TImage; const Curve: TDayCurve; const Points: array of TWeightedDayPoint; ColorCurve,
  ColorPointWeight0, ColorPointWeight1: TColor);
{======================================================================================================================}
var
  Acc: real;
  Max: real;
  Wd: integer;

  procedure CalcAcc;
  const
    FAcc: array[1..13] of real = (1000, 100, 10, 5, 1, 0.5, 0.1, 0.05, 0.01, 0.005, 0.001, 0.0005, 0.0001);
    FWd: array[1..13] of integer =  (0,   0,  0, 0, 0,   1,   1,    2,    2,     3,     3,      4,      4);
  var
    n, LabelHeight: integer;
  begin
    LabelHeight := Image.Canvas.TextHeight('123');
    Acc := 1;
    Wd := 0;
    for n := High(FAcc) downto 1 do
    if Trunc(Max / FAcc[n]) * LabelHeight <= Image.Height - 2 * TopBord then
    begin
      Acc := FAcc[n];
      Wd := FWd[n];
      Exit;
    end;
  end;

var
  i: integer;
  kx, ky: real;
  NewPoint: TPoint;
  Hour, Min, Sec, MSec: word;
  TimePos: integer;
begin
  StartProc('DrawDayCurve');

  Image.Picture.Bitmap.Width := Image.Width;
  Image.Picture.Bitmap.Height := Image.Height;

  { 1. ������� �������� }
  Max := 0;
  for i := 0 to MinPerDay - 1 do
    Max := Math.Max(Max, Curve[i]);
  for i := 0 to High(Points) do
    Max := Math.Max(Max, Points[i].Value);

  if (Max > 1000) then
    Max := 1000 else
  if Max < EPS then
    Max := 1;

  Max := Max * 1.2;

  CalcAcc;
  Max := Round(Max / Acc) * Acc;
  LeftBord :=
    Math.Max(
      Image.Canvas.TextWidth(FloatToStr(Max)),
      Image.Canvas.TextWidth(FloatToStr(Max + Acc))
    ) + 8;
  kx := (Image.Width - 2 * LeftBord) / MinPerDay;
  ky := (Image.Height - 2 * TopBord) / Max;

  with Image.Canvas do
  begin
    { ������� }
    Brush.Color := COLOR_BACK;
    FillRect(Rect(0, 0, Image.Width, Image.Height));

    Pen.Width := 1;
    Pen.Style := psDot;
    Pen.Color := COLOR_AXIS_SUB;
    Font.Color := COLOR_TITLES;
    Font.Size := 9;

    { ����� X }
    for i := 0 to HourPerDay do
    begin
      // TODO: optimize
      MoveTo(LeftBord + Round(i * MinPerHour * kx), TopBord);
      LineTo(LeftBord + Round(i * MinPerHour * kx), Image.Height - TopBord);
      TextOut(
        LeftBord + Round(i * MinPerHour * kx) - (TextWidth(IntToStr(i)) div 2),
        Image.Height - TopBord + 4,
        IntToStr(i)
      );
    end;

    { ����� Y }
    for i := 1 to Round(Max / Acc) do
    begin
      // TODO: optimize
      MoveTo(LeftBord, Image.Height - TopBord - Round(i * Acc * ky));
      LineTo(Image.Width - LeftBord, Image.Height - TopBord - Round(i * Acc * ky));
      TextOut(
        4,
        Image.Height - TopBord - Round(i * Acc * ky)-
        (TextHeight('123') div 2),
        Format('%.' + IntToStr(Wd) + 'f', [i * Acc])
      );
    end;

    { ��� }
    Pen.Style := psSolid;
    Pen.Color := COLOR_AXIS_MAIN;
    MoveTo(LeftBord, TopBord);
    LineTo(LeftBord, Image.Height - TopBord);
    LineTo(Image.Width - LeftBord, Image.Height - TopBord);

    { ������ }
    Pen.Width := 4;
    Pen.Style := psSolid;
    Pen.Color := ColorCurve;

    MoveTo(LeftBord, Image.Height - TopBord - Round(Curve[0] * ky));
    for i := 0 to MinPerDay - 1 do
    begin
      LineTo(LeftBord + Round(i * kx), Image.Height - TopBord - Round(Curve[i] * ky));
    end;

    { ����� }
    Pen.Width := 1;
    for i := 0 to High(Points) do
    begin
      NewPoint := Point(
        LeftBord + Round(Points[i].Time * kx),
        Image.Height - TopBord - Round(Points[i].Value  * ky)
      );

      Brush.Color := RGB(
        Round(GetRValue(ColorPointWeight0) * (1 - Points[i].Weight) + GetRValue(ColorPointWeight1) * Points[i].Weight),
        Round(GetGValue(ColorPointWeight0) * (1 - Points[i].Weight) + GetGValue(ColorPointWeight1) * Points[i].Weight),
        Round(GetBValue(ColorPointWeight0) * (1 - Points[i].Weight) + GetBValue(ColorPointWeight1) * Points[i].Weight)
      );
      Pen.Color := COLOR_BACK;
      Ellipse(
        NewPoint.X - 2 - 1,
        NewPoint.Y - 2 - 1,
        NewPoint.X + 3 + 1,
        NewPoint.Y + 3 + 1
      );
    end;

    {========================================}
    DecodeTime(now, Hour, Min, Sec, MSec);
    Min := Hour * MinPerHour + Min;
    TimePos := Round((Min / MinPerDay) * (Image.Width - 2 * LeftBord));

    Pen.Color := COLOR_TIMEPOS;
    Pen.Style := psSolid;
    MoveTo(LeftBord + TimePos, Image.Height - TopBord);
    LineTo(LeftBord + TimePos, TopBord);
    {========================================}
  end;

  FinishProc;
end;

end.
