"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"

export default function OAuthRedirectPage() {
    const router = useRouter()

    useEffect(() => {
        const fetchJwt = async () => {
            try {
                const res = await fetch("http://localhost:8090/api/oauth-jwt", {
                    credentials: "include", // manda i cookie HttpOnly
                })

                // Prima controllo se la risposta è ok
                if (!res.ok) {
                    const errorText = await res.text()
                    console.error("Errore JWT:", errorText)
                    router.push("/login")
                    return
                }

                // Se ok, estraggo il json con il token
                const data = await res.json()

                localStorage.setItem("jwt", data.token)
                router.push("/userpage")
            } catch (err) {
                console.error("Errore connessione:", err)
                router.push("/login")
            }
        }

        fetchJwt()
    }, [router])

    return <div>Accesso in corso...</div>
}
