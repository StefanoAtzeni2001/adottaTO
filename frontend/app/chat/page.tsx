 "use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardHeader, CardTitle } from "@/components/ui/card"

interface Chat {
    id: number
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

interface Message {
    id: number
    senderId: number
    receiverId: number
    message: string
    timeStamp: string
    seen: boolean
}

export default function ChatPage() {
    const [chats, setChats] = useState<Chat[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [profilesMap, setProfilesMap] = useState<Record<number, UserProfile>>({})
    const [adoptionPostsMap, setAdoptionPostsMap] = useState<Record<number, AdoptionPostDetailDto>>({})
    const [selectedChatId, setSelectedChatId] = useState<number | null>(null)
    const [messages, setMessages] = useState<Message[]>([])

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

    const fetchChatMessages = async (chatId: number) => {
        const token = localStorage.getItem("jwt")

        try {
            const res = await fetch("http://localhost:8090/chat/history", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({ chatId }),
            })

            if (!res.ok) throw new Error("Errore nel recupero dei messaggi")
            const data: Message[] = await res.json()
            setMessages(data)
            setSelectedChatId(chatId)
        } catch (err) {
            console.error("Errore caricamento messaggi:", err)
        }
    }

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
                                key={chat.id}
                                onClick={() => {
                                    fetchChatMessages(chat.id)
                                }}
                                className={`cursor-pointer hover:shadow-lg transition-shadow duration-200 ${
                                    selectedChatId === chat.id ? "border-2 border-primary" : ""
                                }`}
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
                            </Card>
                        )
                    })}
                </div>
            )}

            {/* Sezione messaggi */}
            {selectedChatId && (
                <div className="mt-8 px-6 py-4 bg-gray-50 rounded-md max-w-3xl mx-auto">
                    <h2 className="text-xl font-semibold mb-4">Cronologia chat</h2>
                    <div className="flex flex-col gap-4">
                        {messages.map((msg) => {
                            const isOwn = localStorage.getItem("userId") === msg.senderId.toString()
                            return (
                                <div
                                    key={msg.id}
                                    className={`max-w-[75%] rounded-lg px-3 py-2 text-sm ${
                                        isOwn
                                            ? "bg-primary text-primary-foreground ml-auto"
                                            : "bg-muted"
                                    }`}
                                >
                                    {msg.message /* usa message non content */}
                                    <div className="text-xs text-gray-500 mt-1">
                                        {new Date(msg.timeStamp).toLocaleString()}
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                </div>
            )}

        </div>
    )
}