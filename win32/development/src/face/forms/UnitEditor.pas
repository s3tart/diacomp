unit UnitEditor;

interface

uses
  Forms,
  Controls,
  BusinessObjects,
  DiaryRoutines,
  DiaryInterface;

type
  TFormCommonEditor = class(TAutosetupForm)
  private
    FEntity: TVersioned;
  protected
    property Entity: TVersioned read FEntity write FEntity;

    // deep, null-safe
    class function Clone(X: TVersioned): TVersioned; virtual; abstract;
    class function CreateEditorForm(CreateMode: boolean): TFormCommonEditor; virtual; abstract;
    function ReadEntityFromGUI(): boolean; virtual; abstract;
    procedure ShowEntityInGUI(CreateMode: boolean); virtual; abstract;
    procedure Submit();
  public
    class function ShowEditor(var Entity: TVersioned; CreateMode: boolean): boolean;
  end;

implementation

{ TFormCommonEditor }

{==============================================================================}
class function TFormCommonEditor.ShowEditor(var Entity: TVersioned; CreateMode: boolean): boolean;
{==============================================================================}
var
  Dialog: TFormCommonEditor;
begin
  Dialog := CreateEditorForm(CreateMode);

  Dialog.Entity := Clone(Entity);
  if (CreateMode) then
    Dialog.Entity.ID := CreateCompactGUID();

  Dialog.ShowEntityInGUI(CreateMode);
  Dialog.ShowModal;

  if ((Dialog.ModalResult = mrOk) and Dialog.ReadEntityFromGUI()) then
  begin
    Entity := Clone(Dialog.Entity);
    Entity.Modified;
    Result := True;
  end else
    Result := False;
end;

{==============================================================================}
procedure TFormCommonEditor.Submit;
{==============================================================================}
begin
  if (ReadEntityFromGUI()) then
    ModalResult := mrOK;
end;

end.
