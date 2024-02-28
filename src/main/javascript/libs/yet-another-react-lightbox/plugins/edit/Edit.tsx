import { EditButton } from "./EditButton";
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";


export function Edit({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "edit", <EditButton />),
    ...rest,
  }));
}