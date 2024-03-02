export class HttpError extends Error{
    status: number;
    type: string;
    constructor(status: number, reason: string){
        super(reason);
        this.status = status;
        this.type= 'httpError';
    }
}