import { RemoveButton } from "./RemoveButton";
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";


export function Remove({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "remove", <RemoveButton />),
    ...rest,
  }));
}