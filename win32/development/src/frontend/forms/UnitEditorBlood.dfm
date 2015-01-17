object FormEditorBlood: TFormEditorBlood
  Left = 388
  Top = 407
  BorderIcons = [biSystemMenu]
  BorderStyle = bsDialog
  Caption = 'FormEditorBlood'
  ClientHeight = 267
  ClientWidth = 409
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -13
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  Position = poScreenCenter
  PixelsPerInch = 120
  TextHeight = 16
  object Image: TImage
    Left = 8
    Top = 8
    Width = 80
    Height = 80
    Center = True
  end
  object LabelTime: TLabel
    Left = 104
    Top = 8
    Width = 65
    Height = 16
    Caption = 'LabelTime'
  end
  object LabelValue: TLabel
    Left = 104
    Top = 64
    Width = 69
    Height = 16
    Caption = 'LabelValue'
  end
  object Bevel1: TBevel
    Left = 16
    Top = 175
    Width = 313
    Height = 2
  end
  object LabelFinger: TLabel
    Left = 104
    Top = 112
    Width = 72
    Height = 16
    Caption = 'LabelFinger'
  end
  object ButtonOK: TBitBtn
    Left = 32
    Top = 191
    Width = 120
    Height = 28
    Caption = 'ButtonOK'
    TabOrder = 2
    OnClick = ButtonOKClick
    Glyph.Data = {
      DE010000424DDE01000000000000760000002800000024000000120000000100
      0400000000006801000000000000000000001000000000000000000000000000
      80000080000000808000800000008000800080800000C0C0C000808080000000
      FF0000FF000000FFFF00FF000000FF00FF00FFFF0000FFFFFF00333333333333
      3333333333333333333333330000333333333333333333333333F33333333333
      00003333344333333333333333388F3333333333000033334224333333333333
      338338F3333333330000333422224333333333333833338F3333333300003342
      222224333333333383333338F3333333000034222A22224333333338F338F333
      8F33333300003222A3A2224333333338F3838F338F33333300003A2A333A2224
      33333338F83338F338F33333000033A33333A222433333338333338F338F3333
      0000333333333A222433333333333338F338F33300003333333333A222433333
      333333338F338F33000033333333333A222433333333333338F338F300003333
      33333333A222433333333333338F338F00003333333333333A22433333333333
      3338F38F000033333333333333A223333333333333338F830000333333333333
      333A333333333333333338330000333333333333333333333333333333333333
      0000}
    NumGlyphs = 2
  end
  object ButtonCancel: TBitBtn
    Left = 213
    Top = 191
    Width = 110
    Height = 28
    Cancel = True
    Caption = 'ButtonCancel'
    ModalResult = 2
    TabOrder = 3
    Glyph.Data = {
      DE010000424DDE01000000000000760000002800000024000000120000000100
      0400000000006801000000000000000000001000000000000000000000000000
      80000080000000808000800000008000800080800000C0C0C000808080000000
      FF0000FF000000FFFF00FF000000FF00FF00FFFF0000FFFFFF00333333333333
      3333333333333FFFFF333333000033333388888833333333333F888888FFF333
      000033338811111188333333338833FFF388FF33000033381119999111833333
      38F338888F338FF30000339119933331111833338F388333383338F300003391
      13333381111833338F8F3333833F38F3000039118333381119118338F38F3338
      33F8F38F000039183333811193918338F8F333833F838F8F0000391833381119
      33918338F8F33833F8338F8F000039183381119333918338F8F3833F83338F8F
      000039183811193333918338F8F833F83333838F000039118111933339118338
      F3833F83333833830000339111193333391833338F33F8333FF838F300003391
      11833338111833338F338FFFF883F83300003339111888811183333338FF3888
      83FF83330000333399111111993333333388FFFFFF8833330000333333999999
      3333333333338888883333330000333333333333333333333333333333333333
      0000}
    NumGlyphs = 2
  end
  object ComboFinger: TComboBox
    Left = 104
    Top = 128
    Width = 201
    Height = 24
    Style = csDropDownList
    ItemHeight = 16
    TabOrder = 1
    OnKeyDown = FieldKeyDown
  end
  object EditValue: TEditNumb
    Left = 104
    Top = 80
    Width = 121
    Height = 24
    TabOrder = 0
    Text = 'EditValue'
    OnKeyDown = FieldKeyDown
    AcceptNegative = False
    Decimal = ','
    WarningShow = True
  end
  object TimePicker: TDateTimePicker
    Left = 104
    Top = 24
    Width = 81
    Height = 24
    Date = 41777.158689432870000000
    Time = 41777.158689432870000000
    Kind = dtkTime
    TabOrder = 4
    OnKeyDown = FieldKeyDown
  end
  object DatePicker: TDateTimePicker
    Left = 208
    Top = 24
    Width = 113
    Height = 24
    Date = 41777.159941967590000000
    Time = 41777.159941967590000000
    TabOrder = 5
    OnKeyDown = FieldKeyDown
  end
end
