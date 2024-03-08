
import { PluginProps, addToolbarButton } from "yet-another-react-lightbox";
import { AnnotatedButton } from "./AnnotatedButton";


export function Annotated({ augment }: PluginProps) {
  augment(({ toolbar, ...rest }) => ({
    toolbar: addToolbarButton(toolbar, "annotated", <AnnotatedButton />),
    ...rest,
  }));
}