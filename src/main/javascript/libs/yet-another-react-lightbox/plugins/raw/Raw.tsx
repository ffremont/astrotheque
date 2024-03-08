import { RawButton } from "./RawButton";
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";


export function Raw({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "raw", <RawButton />),
    ...rest,
  }));
}