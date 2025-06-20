"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

interface Chat {
    chatId: number
    ownerId: number
    adoptionPostId: number
}

interface AdoptionPostDetailDto {
    id: number
    name: string
    species: string
    breed: string
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
    const [adoptionPostsMap, setAdoptionPostsMap] = useState<Record<number, AdoptionPostDetailDto>>({})
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

                const uniqueOwnerIds = Array.from(new Set(data.map(chat => chat.ownerId)))
                const uniquePostIds = Array.from(new Set(data.map(chat => chat.adoptionPostId)))

                Promise.all(
                    uniqueOwnerIds.map(async (id) => {
                        const res = await fetch(`http://localhost:8090/api/profile/${id}`)
                        if (!res.ok) throw new Error(`Errore fetch profilo ownerId ${id}`)
                        return res.json()
                    })
                ).then((profiles: UserProfile[]) => {
                    const map: Record<number, UserProfile> = {}
                    uniqueOwnerIds.forEach((id, idx) => {
                        map[id] = profiles[idx]
                    })
                    setProfilesMap(map)
                }).catch((err) => {
                    console.error("Errore caricamento profili owner:", err)
                })

                Promise.all(
                    uniquePostIds.map(async (id) => {
                        const res = await fetch(`http://localhost:8090/get-by-id/${id}`)
                        if (!res.ok) throw new Error(`Errore fetch postId ${id}`)
                        return res.json()
                    })
                ).then((posts: AdoptionPostDetailDto[]) => {
                    const map: Record<number, AdoptionPostDetailDto> = {}
                    uniquePostIds.forEach((id, idx) => {
                        map[id] = posts[idx]
                    })
                    setAdoptionPostsMap(map)
                }).catch((err) => {
                    console.error("Errore caricamento adozioni:", err)
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
            <h1 className="text-3xl font-bold mb-6">Le mie chat</h1>
            {chats.length === 0 ? (
                <p>Non hai ancora chat attive.</p>
            ) : (
                <div className="flex flex-col gap-4 max-w-md">
                    {chats.map(chat => {
                        const profile = profilesMap[chat.ownerId]
                        const adoptionPost = adoptionPostsMap[chat.adoptionPostId]

                        const profileImg = profile?.profilePicture?.trim()
                            ? profile.profilePicture.startsWith("http")
                                ? profile.profilePicture
                                : `/${profile.profilePicture.replace(/^\/+/, "")}`
                            : "/default-avatar.svg"

                        return (
                            <Card
                                key={chat.chatId}
                                onClick={() => router.push(`/chat/${chat.chatId}`)}
                                className="cursor-pointer hover:shadow-lg transition-shadow duration-200"
                            >
                                <CardHeader className="flex items-center space-x-4">
                                    <img
                                        src={profileImg}
                                        alt={`${profile?.name ?? ""} ${profile?.surname ?? ""}`}
                                        className="w-12 h-12 rounded-full object-cover"
                                    />
                                    <div>
                                        <CardTitle className="text-lg">
                                            {profile ? `${profile.name} ${profile.surname}` : "Utente sconosciuto"}
                                        </CardTitle>
                                        {adoptionPost && (
                                            <p className="text-sm text-muted-foreground">
                                                {adoptionPost.name} – {adoptionPost.species} ({adoptionPost.breed})
                                            </p>
                                        )}
                                    </div>
                                </CardHeader>
                                {/* CardContent può essere mantenuto vuoto o rimosso se non serve */}
                                <CardContent />
                            </Card>
                        )
                    })}
                </div>
            )}
        </div>
    )
}
