
type FooterProps = {
    version: string
    totalItems: number,
    totalBytes: number
}

export const Footer = ({version, totalBytes,totalItems}: FooterProps) => <div className="appFooter">
    <div className="version">version : {version}</div>
    <div>|</div>
    {totalBytes > 0 && <><div className="bytes">{(totalBytes/ (1024*1024*1024)).toFixed(3)} Go </div>
    <div>|</div></>}
    <div className="items">{totalItems} éléments</div>
</div>