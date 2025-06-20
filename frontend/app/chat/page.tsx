"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

interface Chat {
    chatId: number
    ownerId: number
    adoptionPostId: number
}

interface UserProfile {
    name: string
    surname: string
    email: string
    profilePicture?: string | null
}

export default function ChatPage() {
    const [chats, setChats] = useState<Chat[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [profilesMap, setProfilesMap] = useState<Record<number, UserProfile>>({})
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            router.push("/login")
            return
        }

        fetch("http://localhost:8090/chat/chats", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then(async (res) => {
                if (!res.ok) throw new Error("Errore nel recupero delle chat")
                return res.json()
            })
            .then((data: Chat[]) => {
                setChats(data)
                setLoading(false)

                // Estrai tutti gli ownerId unici
                const uniqueOwnerIds = Array.from(new Set(data.map(chat => chat.ownerId)))

                // Fetch dei profili degli owner
                Promise.all(
                    uniqueOwnerIds.map(async (id) => {
                        const res = await fetch(`http://localhost:8090/api/profile/${id}`)
                        if (!res.ok) throw new Error(`Errore fetch profilo ownerId ${id}`)
                        return res.json()
                    })
                )
                    .then((profiles: UserProfile[]) => {
                        const map: Record<number, UserProfile> = {}
                        uniqueOwnerIds.forEach((id, idx) => {
                            map[id] = profiles[idx]
                        })
                        setProfilesMap(map)
                    })
                    .catch((err) => {
                        console.error("Errore caricamento profili owner:", err)
                    })
            })
            .catch((err) => {
                console.error("Errore durante la richiesta:", err)
                setError("Errore durante il caricamento delle chat")
                setLoading(false)
            })
    }, [router])

    if (loading) return <div>Caricamento chat...</div>
    if (error) return <div>{error}</div>

    return (
        <div className="container py-6">
            <h1 className="text-3xl font-bold mb-4">Le mie chat</h1>
            {chats.length === 0 ? (
                <p>Non hai ancora chat attive.</p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    {chats.map(chat => {
                        const profile = profilesMap[chat.ownerId]

                        const profileImg = profile?.profilePicture?.trim()
                            ? profile.profilePicture.startsWith("http")
                                ? profile.profilePicture
                                : `/${profile.profilePicture.replace(/^\/+/, "")}`
                            : "/default-avatar.svg"

                        return (
                            <Card key={chat.chatId}>
                                <CardHeader className="flex items-center space-x-4">
                                    <img
                                        src={profileImg}
                                        alt={`${profile?.name ?? ""} ${profile?.surname ?? ""}`}
                                        className="w-12 h-12 rounded-full object-cover"
                                    />
                                    <CardTitle>
                                        {profile ? `${profile.name} ${profile.surname}` : "Utente sconosciuto"}
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <Button
                                        className="mt-2"
                                        onClick={() => router.push(`/chat/${chat.chatId}`)}
                                    >
                                        Vai alla chat
                                    </Button>
                                </CardContent>
                            </Card>
                        )
                    })}
                </div>
            )}
        </div>
    )
}
