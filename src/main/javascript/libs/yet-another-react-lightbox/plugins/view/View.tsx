import { ViewButton } from "./ViewButton";
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";


export function View({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "view", <ViewButton />),
    ...rest,
  }));
}