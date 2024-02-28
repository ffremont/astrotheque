

interface FetchMethods {
    call<T>(url: string, options: RequestInit): Promise<T>,
    get<T>(url: string): Promise<T>
    post<T, U>(url: string, body: U): Promise<T>
}

export const useFetch = (defaultTimeout = 5000): FetchMethods => {
    const call = async <T>(url: string, options: RequestInit) => {
            try{
                const response = await fetch(url, options);

            if (!response.ok) {
                throw new Error('Erreur lors de appel')
            }

            const responseData: T = await response.json()
            return responseData
        }catch(e){
            console.error(e);
            throw e;
        }
       
    }

    const get = async <T>(url: string) => {
        return call<T>(url, { signal: AbortSignal.timeout(defaultTimeout) })
    }

    const post = async <T, U>(url: string, body: U) => {
        return call<T>(url, {
            headers: {
                'Content-Type': 'application/json',
            },
            method:'POST',
            body: JSON.stringify(body),
            signal: AbortSignal.timeout(defaultTimeout),
        })
    }

    return {
        get,
        post,
        call
    }
}
