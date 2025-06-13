"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"

interface UserProfile {
    name: string
    surname: string
    email: string
}

export default function Page() {
    const [profile, setProfile] = useState<UserProfile | null>(null)
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            router.push("/login") // redirect se il token non c'è
            return
        }

        fetch("http://localhost:8083/profile", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(async res => {
                if (!res.ok) throw new Error("Token non valido")
                return res.json()
            })
            .then(data => {
                setProfile(data)
            })
            .catch(() => {
                localStorage.removeItem("jwt")
                router.push("/login") // redirect se token invalido
            })
    }, [router])

    const handleLogout = () => {
        localStorage.removeItem("jwt")
        router.push("/login")
    }

    if (!profile) return <div>Caricamento...</div>

    return (
        <div className="container">
            <Avatar>
                <AvatarImage src="https://github.com/shadcn.png" />
                <AvatarFallback>{profile.name.charAt(0)}{profile.surname.charAt(0)}</AvatarFallback>
            </Avatar>
            
            <Button variant="outline" onClick={handleLogout}>
                Logout
            </Button>

            <h1 className="scroll-m-20 text-center text-4xl font-extrabold tracking-tight text-balance">
                {profile.name} {profile.surname}
            </h1>

            <h4 className="scroll-m-20 text-xl font-semibold tracking-tight">
                {profile.email}
            </h4>

            <div className="flex flex-wrap items-center gap-2 md:flex-row">
                <Button>Pubblica un nuovo annuncio!</Button>
                <Button variant="outline" onClick={handleLogout}>Logout</Button>
            </div>

            <Separator className="my-4" />
        </div>
    )
}
