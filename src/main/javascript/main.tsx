import React from 'react'
import ReactDOM from 'react-dom/client'
import  App  from './App'
import './index.css'
import "yet-another-react-lightbox/styles.css";
import { BrowserRouter } from 'react-router-dom'

/**
* Groups elements from an iterable into an object based on a callback function.
*
* @template T, K
* @param {Iterable<T>} iterable - The iterable to group.
* @param {function(T, number): K} callbackfn - The callback function to
* determine the grouping key.
* @returns {Object.<string, T[]>} An object where keys are the grouping keys
* and values are arrays of grouped elements.
*/
(window.Object as any).groupBy ??= function groupBy (iterable: any, callbackfn: any) {
 const obj = Object.create(null)
 let i = 0
 for (const value of iterable) {
   const key = callbackfn(value, i++)
   key in obj ? obj[key].push(value) : (obj[key] = [value])
 }
 return obj
}


ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <BrowserRouter>
            <App />
        </BrowserRouter>
    </React.StrictMode>
)
