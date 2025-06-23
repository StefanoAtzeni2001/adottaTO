"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardHeader, CardTitle } from "@/components/ui/card"

interface Chat {
    id: number
    ownerId: number
    adopterId: number
    adoptionPostId: number
}

interface AdoptionPostDetailDto {
    id: number
    name: string
    species: string
    breed: string
}

interface UserProfile {
    id: number
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
    const [newMessage, setNewMessage] = useState("")

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

                const uniqueUserIds = Array.from(new Set(data.flatMap(chat => [chat.ownerId, chat.adopterId])))
                const uniquePostIds = Array.from(new Set(data.map(chat => chat.adoptionPostId)))

                Promise.all(
                    uniqueUserIds.map(async (id) => {
                        const res = await fetch(`http://localhost:8090/api/profile/${id}`)
                        if (!res.ok) throw new Error(`Errore fetch profilo userId ${id}`)
                        return res.json()
                    })
                ).then((profiles: UserProfile[]) => {
                    const map: Record<number, UserProfile> = {}
                    uniqueUserIds.forEach((id, idx) => {
                        map[id] = profiles[idx]
                    })
                    setProfilesMap(map)
                }).catch((err) => {
                    console.error("Errore caricamento profili:", err)
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

    const handleSendMessage = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        const token = localStorage.getItem("jwt")
        const senderId = localStorage.getItem("userId")

        if (!token || !senderId || !selectedChatId) {
            alert("Utente non autenticato o chat non selezionata")
            return
        }

        if (!newMessage.trim()) {
            alert("Inserisci un messaggio")
            return
        }

        const selectedChat = chats.find((chat) => chat.id === selectedChatId)
        if (!selectedChat) {
            alert("Chat non trovata")
            return
        }

        const senderIdNum = Number(senderId)
        let receiverId: number

        if (senderIdNum === selectedChat.ownerId) {
            receiverId = selectedChat.adopterId
        } else if (senderIdNum === selectedChat.adopterId) {
            receiverId = selectedChat.ownerId
        } else {
            alert("Utente non coinvolto nella chat")
            return
        }

        try {
            const res = await fetch("http://localhost:8090/chat/send", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    chatId: selectedChatId,
                    senderId: senderIdNum,
                    receiverId,
                    adoptionPostId: selectedChat.adoptionPostId,
                    message: newMessage.trim(),
                }),
            })

            if (!res.ok) throw new Error("Errore invio messaggio")

            const savedMessage = await res.json()
            setMessages((prev) => [...prev, savedMessage])
            setNewMessage("")
        } catch (err) {
            console.error("Errore invio messaggio:", err)
            alert("Errore durante l'invio del messaggio")
        }
    }

    if (loading) return <div>Caricamento chat...</div>
    if (error) return <div>{error}</div>

    return (
        <div className="h-[calc(100vh-5rem)] px-4 py-6">
            <h1 className="text-3xl font-bold mb-4">Le mie chat</h1>
            {chats.length === 0 ? (
                <p>Non hai ancora chat attive.</p>
            ) : (
                <div className="flex h-full border rounded-lg overflow-hidden shadow-sm">
                    {/* Lista chat a sinistra */}
                    <div className="w-1/3 bg-white border-r overflow-y-auto">
                        <div className="flex flex-col gap-2 p-4">
                            {chats.map(chat => {
                                const userId = Number(localStorage.getItem("userId"))
                                const isOwner = userId === chat.ownerId
                                const otherUserId = isOwner ? chat.adopterId : chat.ownerId
                                const profile = profilesMap[otherUserId]
                                const adoptionPost = adoptionPostsMap[chat.adoptionPostId]

                                const profileImg = profile?.profilePicture?.trim()
                                    ? profile.profilePicture.startsWith("http")
                                        ? profile.profilePicture
                                        : `/${profile.profilePicture.replace(/^\/+/, "")}`
                                    : "/default-avatar.svg"

                                return (
                                    <Card
                                        key={chat.id}
                                        onClick={() => fetchChatMessages(chat.id)}
                                        className={`cursor-pointer hover:shadow transition duration-200 ${
                                            selectedChatId === chat.id ? "border-2 border-primary" : ""
                                        }`}
                                    >
                                        <CardHeader className="flex items-center space-x-4">
                                            <img
                                                src={profileImg}
                                                alt={`${profile?.name ?? ""} ${profile?.surname ?? ""}`}
                                                className="w-10 h-10 rounded-full object-cover"
                                            />
                                            <div>
                                                <CardTitle className="text-base">
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
                    </div>

                    {/* Conversazione a destra */}
                    <div className="w-2/3 flex flex-col justify-between overflow-y-auto bg-gray-50 p-4">
                        {selectedChatId ? (
                            <>
                                <div className="flex-1 overflow-y-auto pr-2 space-y-3">
                                    <h2 className="text-xl font-semibold mb-2">Cronologia chat</h2>
                                    {messages.map((msg) => {
                                        const isSender = localStorage.getItem("userId") === msg.senderId.toString()
                                        return (
                                            <div
                                                key={msg.id}
                                                className={`max-w-[75%] rounded-lg px-3 py-2 text-sm ${
                                                    isSender ? "bg-primary text-primary-foreground ml-auto" : "bg-muted"
                                                }`}
                                            >
                                                {msg.message}
                                                <div className="text-xs text-gray-500 mt-1">
                                                    {new Date(msg.timeStamp).toLocaleString()}
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>

                                <form onSubmit={handleSendMessage} className="mt-4 flex items-center gap-2">
                                    <input
                                        id="message"
                                        value={newMessage}
                                        onChange={(e) => setNewMessage(e.target.value)}
                                        placeholder="Scrivi un messaggio..."
                                        className="flex-1 h-10 rounded-md border px-3 py-2 text-sm shadow-xs focus-visible:ring-2 focus-visible:ring-ring/50 transition-colors"
                                        autoComplete="off"
                                    />
                                    <button
                                        type="submit"
                                        className="size-9 rounded-full bg-primary text-primary-foreground inline-flex items-center justify-center hover:bg-primary/90"
                                    >
                                        <svg className="size-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                            <path d="m5 12 7-7 7 7" />
                                            <path d="M12 19V5" />
                                        </svg>
                                        <span className="sr-only">Invia</span>
                                    </button>
                                </form>
                            </>
                        ) : (
                            <div className="flex-1 flex items-center justify-center text-gray-500">
                                Seleziona una chat per iniziare
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    )

}
