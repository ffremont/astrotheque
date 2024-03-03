import { HttpError } from "../types/HttpError";


interface FetchMethods {
    call<T>(url: string, options: RequestInit): Promise<T>,
    get<T>(url: string): Promise<T>
    post<T, U>(url: string, body: U): Promise<T>
}

export const useFetch = (defaultTimeout = 5000): FetchMethods => {
    const call = async <T>(url: string, options: RequestInit) => {
            let status = 0;
            try{
                const response = await fetch(url, options);
                status = response.status;
            if (!response.ok) {
                throw new Error('Erreur lors de appel')
            }

            const responseData: T = status !== 204 ? await response.json() : null
            return responseData
        }catch(e: Error  | any){
            console.error(e);
            throw new HttpError(status, e?.message);
        }
    }

    const get = async <T>(url: string) => {
        return call<T>(url, { signal: AbortSignal.timeout(defaultTimeout) })
    }

    const post = async <T, U>(url: string, body: U) => {
        const isFormData = body instanceof FormData;
        return call<T>(url, {
            headers: isFormData ? {} : {
                'Content-Type': 'application/json'
            },
            method:'POST',
            body:  isFormData ? body : JSON.stringify(body),
            signal: AbortSignal.timeout(defaultTimeout),
        })
    }

    return {
        get,
        post,
        call
    }
}
