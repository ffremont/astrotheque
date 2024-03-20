import { BinariesButton } from "./BinariesButton";
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";


export function Binaries({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "binaries", <BinariesButton />),
    ...rest,
  }));
}