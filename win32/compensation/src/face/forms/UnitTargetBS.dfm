object FormTargetBS: TFormTargetBS
  Left = 372
  Top = 340
  BorderIcons = [biSystemMenu]
  BorderStyle = bsDialog
  Caption = #1062#1077#1083#1077#1074#1086#1081' '#1057#1050
  ClientHeight = 123
  ClientWidth = 262
  Color = clBtnFace
  Constraints.MinHeight = 150
  Constraints.MinWidth = 270
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -13
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnShow = FormShow
  DesignSize = (
    262
    123)
  PixelsPerInch = 120
  TextHeight = 16
  object ImageTargetBS: TImage
    Left = 24
    Top = 16
    Width = 25
    Height = 50
    AutoSize = True
    Transparent = True
  end
  object LabelHint: TLabel
    Left = 96
    Top = 24
    Width = 147
    Height = 16
    Caption = #1062#1077#1083#1077#1074#1086#1077' '#1079#1085#1072#1095#1077#1085#1080#1077' '#1057#1050':'
  end
  object Bevel1: TBevel
    Left = 8
    Top = 73
    Width = 236
    Height = 2
    Anchors = [akLeft, akTop, akRight]
  end
  object ButtonCancel: TBitBtn
    Left = 127
    Top = 88
    Width = 110
    Height = 28
    Cancel = True
    Caption = #1054#1090#1084#1077#1085#1072
    TabOrder = 0
    OnClick = ButtonCancelClick
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
  object ButtonSave: TBitBtn
    Left = 24
    Top = 72
    Width = 120
    Height = 28
    Caption = #1054#1050
    TabOrder = 1
    OnClick = ButtonSaveClick
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
  object EditBS: TEditNumb
    Left = 176
    Top = 48
    Width = 121
    Height = 24
    TabOrder = 2
    Text = 'EditBS'
    OnKeyDown = EditBSKeyDown
    Decimal = ','
  end
end
