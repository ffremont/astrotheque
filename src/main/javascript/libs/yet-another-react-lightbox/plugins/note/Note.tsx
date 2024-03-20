
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";
import { NoteButton } from "./NoteButton";


export function Note({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "note", <NoteButton />),
    ...rest,
  }));
}