object FormFirstMan: TFormFirstMan
  Left = 340
  Top = 417
  Anchors = [akLeft, akBottom]
  BorderIcons = []
  BorderStyle = bsDialog
  BorderWidth = 12
  Caption = #1044#1086#1073#1088#1086' '#1087#1086#1078#1072#1083#1086#1074#1072#1090#1100'!'
  ClientHeight = 154
  ClientWidth = 293
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -13
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnShow = FormShow
  PixelsPerInch = 120
  TextHeight = 16
  object GroupBox1: TGroupBox
    Left = 0
    Top = 0
    Width = 293
    Height = 105
    Align = alTop
    Caption = #1043#1076#1077' '#1089#1086#1079#1076#1072#1090#1100' '#1103#1088#1083#1099#1082#1080'?'
    TabOrder = 0
    DesignSize = (
      293
      105)
    object CheckBoxDesktop: TCheckBox
      Left = 16
      Top = 24
      Width = 261
      Height = 17
      Anchors = [akLeft, akTop, akRight]
      Caption = #1053#1072' '#1088#1072#1073#1086#1095#1077#1084' '#1089#1090#1086#1083#1077
      Checked = True
      State = cbChecked
      TabOrder = 0
    end
    object CheckBoxMenu: TCheckBox
      Left = 16
      Top = 48
      Width = 261
      Height = 17
      Anchors = [akLeft, akTop, akRight]
      Caption = #1042' '#1084#1077#1085#1102' '#1055#1091#1089#1082'\'#1055#1088#1086#1075#1088#1072#1084#1084#1099
      Checked = True
      State = cbChecked
      TabOrder = 1
    end
    object CheckBoxQLaunch: TCheckBox
      Left = 16
      Top = 72
      Width = 261
      Height = 17
      Anchors = [akLeft, akTop, akRight]
      Caption = #1053#1072' '#1087#1072#1085#1077#1083#1080' '#1073#1099#1089#1090#1088#1086#1075#1086' '#1079#1072#1087#1091#1089#1082#1072
      Checked = True
      State = cbChecked
      TabOrder = 2
    end
  end
  object ButtonOK: TBitBtn
    Left = 96
    Top = 120
    Width = 113
    Height = 25
    Caption = #1053#1072#1095#1072#1090#1100
    TabOrder = 1
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
end
