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

    const handleSendMessage = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        console.log("Invio messaggio triggered");

        const token = localStorage.getItem("jwt");
        const senderId = localStorage.getItem("userId");


        console.log("token:", token);
        console.log("senderId:", senderId);
        console.log("selectedChatId:", selectedChatId);

        if (!token || !selectedChatId) {
            alert("Utente non autenticato o chat non selezionata");
            return;
        }

        if (!newMessage.trim()) {
            alert("Inserisci un messaggio");
            return;
        }

        // Trova la chat selezionata per ricavare ownerId e adoptionPostId
        const selectedChat = chats.find((chat) => chat.id === selectedChatId);
        if (!selectedChat) {
            alert("Chat non trovata");
            return;
        }

        // Definisci receiverId in base a chi è owner o adopter:
        // Se sender è owner, receiver è adopter, e viceversa.
        // Qui assumo senderId è adopterId (in base al backend) - modificalo se serve.
        const senderIdNum = Number(senderId);
        let receiverId: number;

        if (senderIdNum === selectedChat.ownerId) {
            // Se il sender è owner, il receiver è l'adopter
            // Nel tuo modello manca adopterId, forse il sender è adopter
            // Qui devi avere adopterId da qualche parte, o ricavarlo
            alert("Impossibile determinare receiverId (manca adopterId nel modello)");
            return;
        } else {
            // Sender è adopter, receiver è owner
            receiverId = selectedChat.ownerId;
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
                    receiverId: receiverId,
                    adoptionPostId: selectedChat.adoptionPostId,
                    message: newMessage.trim(),
                }),
            });

            if (!res.ok) throw new Error("Errore invio messaggio");

            const savedMessage = await res.json();
            setMessages((prev) => [...prev, savedMessage]);
            setNewMessage("");
        } catch (err) {
            console.error("Errore fetch:", err);
            alert("Errore durante l'invio del messaggio");
        }
    };

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
                                onClick={() => fetchChatMessages(chat.id)}
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
                                        isOwn ? "bg-primary text-primary-foreground ml-auto" : "bg-muted"
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

                    {/* Footer con input */}
                    <form onSubmit={handleSendMessage} className="mt-6 relative w-full flex items-center gap-2">
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
                </div>
            )}
        </div>
    )
}
