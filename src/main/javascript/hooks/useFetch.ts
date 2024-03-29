import { HttpError } from '../types/HttpError'

interface FetchMethods {
    call<T>(url: string, options: RequestInit): Promise<T>
    get<T>(url: string): Promise<T>
    delete(url: string): Promise<void>
    post<T, U>(url: string, body: U): Promise<T>
    put<T, U>(url: string, body: U): Promise<T>
    patch<T, U>(url: string, body: U): Promise<T>
}

export const useFetch = (defaultTimeout = 5000): FetchMethods => {
    const call = async <T>(url: string, options: RequestInit) => {
        let status = 0
        try {
            const response = await fetch(url, options)
            status = response.status
            if (!response.ok) {
                throw new Error('Erreur lors de appel')
            }

            const responseData: T =
                status !== 204 ? await response.json() : null
            return responseData
        } catch (e: Error | any) {
            console.error(e)
            throw new HttpError(status, e?.message)
        }
    }

    const get = <T>(url: string) => {
        return call<T>(url, { signal: AbortSignal.timeout(defaultTimeout) })
    }

    const del = async (url: string) => {
        await call(url, { method:'DELETE', signal: AbortSignal.timeout(defaultTimeout) })
    }

    const _withBody = async <T, U>(method: string, url: string, body: U) => {
        const isFormData = body instanceof FormData
        return call<T>(url, {
            headers: isFormData
                ? {}
                : {
                      'Content-Type': 'application/json',
                  },
            method: method,
            body: isFormData ? body : JSON.stringify(body),
            signal: AbortSignal.timeout(defaultTimeout),
        })
    }

    const post = <T, U>(url: string, body: U) => {
        return _withBody('POST', url, body) as Promise<T>
    }

    const patch = <T, U>(url: string, body: U) => {
        return _withBody('PATCH', url, body) as Promise<T>
    }

    const put = <T, U>(url: string, body: U) => {
        return _withBody('PUT', url, body) as Promise<T>
    }

    return {
        get,
        post,
        put,
        patch,
        delete: del,
        call,
    }
}
