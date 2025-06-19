"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import EditProfile from "@/components/EditProfile"
import PostAdoption from "@/components/CreateAdoptionPost"

interface UserProfile {
    name: string
    surname: string
    email: string
    profilePicture: string
}

export default function UserPage() {
    const [profile, setProfile] = useState<UserProfile | null>(null)
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            router.push("/login")
            return
        }

        fetch("http://localhost:8090/profile", {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(async res => {
                if (!res.ok) throw new Error("Token non valido")
                return res.json()
            })
            .then(setProfile)
            .catch(() => {
                localStorage.removeItem("jwt")
                router.push("/login")
            })
    }, [router])

    const handleProfileUpdate = async (name: string, surname: string) => {
        const token = localStorage.getItem("jwt")
        const res = await fetch("http://localhost:8090/api/profile/update", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify({ name, surname })
        })

        if (res.ok) {
            alert("Profilo aggiornato con successo")
            setProfile(prev => prev ? { ...prev, name, surname } : null)
        } else {
            alert("Errore durante l'aggiornamento del profilo")
        }
    }

    const handleLogout = () => {
        localStorage.removeItem("jwt")
        router.push("/login")
    }

    if (!profile) return <div>Caricamento...</div>

    return (
        <div className="container py-6">
            <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
                <Avatar className="w-32 h-32">
                    <AvatarImage src={profile.profilePicture ?? "/default-avatar.svg"} />
                    <AvatarFallback>{profile.name[0]}{profile.surname[0]}</AvatarFallback>
                </Avatar>

                <div className="flex flex-col items-center md:items-start gap-2">
                    <div className="flex items-center gap-4">
                        <h1 className="text-4xl font-bold">{profile.name} {profile.surname}</h1>
                        <EditProfile profile={profile} onUpdateAction={handleProfileUpdate} />
                        <Button variant="destructive" onClick={handleLogout}>Logout</Button>
                    </div>
                    <p className="text-lg text-gray-600">{profile.email}</p>
                    <PostAdoption />
                </div>
            </div>

            <Separator className="my-8" />
        </div>
    )
}
