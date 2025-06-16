"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"

export default function OAuthRedirectPage() {
    const router = useRouter()

    useEffect(() => {
        const fetchJwt = async () => {
            try {
                const res = await fetch("http://localhost:8083/api/oauth-jwt", {
                    credentials: "include", // manda i cookie
                })
                const data = await res.json()

                if (res.ok) {
                    localStorage.setItem("jwt", data.token)
                    router.push("/userpage")
                } else {
                    console.error("Errore JWT:", data)
                    router.push("/login")
                }
            } catch (err) {
                console.error("Errore connessione:", err)
                router.push("/login")
            }
        }

        fetchJwt()
    }, [])

    return <div>Accesso in corso...</div>
}
