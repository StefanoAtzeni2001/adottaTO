"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardHeader, CardTitle } from "@/components/ui/card"

interface Chat {
    id: number
    ownerId: number
    adopterId: number
    adoptionPostId: number
    requestFlag?: boolean
    acceptedFlag?: boolean
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
    type?: "text" | "request"  // messaggio normale o richiesta adozione
    accepted?: boolean
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
    const [userId, setUserId] = useState<number | null>(null)

    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        const userIdStr = localStorage.getItem("userId")
        if (!token || !userIdStr) {
            router.push("/login")
            return
        }
        setUserId(Number(userIdStr))

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
        if (!token || !userId || !selectedChatId) {
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

        let receiverId: number
        if (userId === selectedChat.ownerId) receiverId = selectedChat.adopterId
        else if (userId === selectedChat.adopterId) receiverId = selectedChat.ownerId
        else {
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
                    senderId: userId,
                    receiverId,
                    adoptionPostId: selectedChat.adoptionPostId,
                    message: newMessage.trim(),
                    type: "text",
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

    const handleSendRequest = async (chat: Chat) => {
        const token = localStorage.getItem("jwt")
        if (!token || !userId) {
            alert("Utente non autenticato")
            return
        }

        if (userId === chat.ownerId) {
            alert("Non puoi inviare una richiesta su un tuo annuncio")
            return
        }

        try {
            const res = await fetch("http://localhost:8090/chat/sendRequest", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    chatId: chat.id,
                    adopterId: userId,
                }),
            })

            if (!res.ok) throw new Error("Errore nell'invio della richiesta")

            setChats((prev) =>
                prev.map((c) =>
                    c.id === chat.id ? { ...c, requestFlag: false } : c
                )
            )

            alert("Richiesta inviata con successo!")
        } catch (err) {
            console.error("Errore invio richiesta:", err)
            alert("Errore durante l'invio della richiesta")
        }
    }

    const handleRespondRequest = async (chatId: number, accept: boolean) => {
        const token = localStorage.getItem("jwt")
        if (!token || !userId) return

        try {
            const res = await fetch(
                accept ? "http://localhost:8090/chat/acceptRequest" : "http://localhost:8090/chat/rejectRequest",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify({ chatId }),
                }
            )


            if (!res.ok) throw new Error("Errore durante la risposta alla richiesta")

            setChats((prev) =>
                prev.map((c) =>
                    c.id === chatId ? { ...c, requestFlag: accept } : c
                )
            )

            alert(`Richiesta ${accept ? "accettata" : "rifiutata"}!`)
        } catch (err) {
            console.error(err)
            alert("Errore durante la risposta alla richiesta")
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
                                if (!userId) return null
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

                                        {/* Se l'utente NON è owner, mostra il pulsante "Invia richiesta" solo se request non è true */}
                                        {!isOwner && chat.requestFlag !== true && (
                                            <button
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handleSendRequest(chat)
                                                }}
                                                className="mt-2 w-full bg-green-600 text-white py-1 rounded hover:bg-green-700 transition"
                                            >
                                                Invia richiesta adozione
                                            </button>
                                        )}

                                        {/* Se l'utente è owner, mostra il pulsante "Accetta/Rifiuta richiesta"*/}
                                        {isOwner && chat.requestFlag === true && chat.acceptedFlag === false && (
                                            <div>
                                                <button
                                                    onClick={(e) => {
                                                        e.stopPropagation()
                                                        handleRespondRequest(chat.id, true)
                                                    }}
                                                    className="mt-2 w-full bg-green-600 text-white py-1 rounded hover:bg-green-700 transition"
                                                >
                                                    Accetta richiesta adozione
                                                </button>

                                                <button
                                                    onClick={(e) => {
                                                        e.stopPropagation()
                                                        handleRespondRequest(chat.id, false)
                                                    }}
                                                    className="mt-2 w-full bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                                                >
                                                    Rifiuta richiesta adozione
                                                </button>
                                            </div>
                                        )}


                                        {/* Mostra stato richiesta */}
                                        {chat.acceptedFlag === true && (
                                            <div className="mt-2 px-3 py-1 text-sm bg-green-100 text-green-800 rounded">
                                                Richiesta accettata
                                            </div>
                                        )}
                                        {chat.requestFlag === true && chat.acceptedFlag === false && (
                                            <div className="mt-2 px-3 py-1 text-sm bg-yellow-100 text-yellow-800 rounded">
                                                Richiesta in attesa di risposta
                                            </div>
                                        )}
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
                                        if (!userId) return null
                                        const isSender = userId === msg.senderId

                                        if (msg.type === "request") {
                                            const isOwner = chats.find(c => c.id === selectedChatId)?.ownerId === userId
                                            return (
                                                <div
                                                    key={msg.id}
                                                    className={`max-w-[75%] rounded-lg px-3 py-2 text-sm ${
                                                        isSender ? "bg-primary text-primary-foreground ml-auto" : "bg-yellow-200"
                                                    }`}
                                                >
                                                    <strong>Richiesta di adozione</strong>
                                                    <div>Stato: {msg.accepted === true ? "Accettata" : msg.accepted === false ? "In attesa o rifiutata" : "Non definita"}</div>
                                                    <div className="text-xs text-gray-500 mt-1">
                                                        {new Date(msg.timeStamp).toLocaleString()}
                                                    </div>
                                                    {/* Se sono owner e la richiesta non è ancora accettata (accepted !== true), mostra pulsanti */}
                                                    {isOwner && (
                                                        <div className="mt-2 flex gap-2">
                                                            <button
                                                                onClick={() => handleRespondRequest(selectedChatId, true)}
                                                                className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
                                                            >
                                                                Accetta
                                                            </button>
                                                            <button
                                                                onClick={() => handleRespondRequest(selectedChatId, false)}
                                                                className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                                                            >
                                                                Rifiuta
                                                            </button>
                                                        </div>
                                                    )}
                                                </div>
                                            )
                                        }

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

                                <form onSubmit={handleSendMessage} className="mt-4 flex gap-2">
                                    <input
                                        type="text"
                                        className="flex-grow border rounded px-3 py-2"
                                        placeholder="Scrivi un messaggio..."
                                        value={newMessage}
                                        onChange={(e) => setNewMessage(e.target.value)}
                                    />
                                    <button
                                        type="submit"
                                        className="bg-primary text-primary-foreground px-4 rounded hover:bg-primary/90"
                                    >
                                        Invia
                                    </button>
                                </form>
                            </>
                        ) : (
                            <p>Seleziona una chat per visualizzare i messaggi.</p>
                        )}
                    </div>
                </div>
            )}
        </div>
    )
}
