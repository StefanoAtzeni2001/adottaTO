"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

interface Chat {
    id: number
    participantIds?: number[] // ora opzionale
    // aggiungi altri campi se esistono
}

export default function ChatPage() {
    const [chats, setChats] = useState<Chat[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
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
            }
        })
            .then(async res => {
                if (!res.ok) throw new Error("Errore nel recupero delle chat")
                return res.json()
            })
            .then(data => {
                setChats(data)
                setLoading(false)
            })
            .catch(err => {
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
                    {chats.map(chat => (
                        <Card key={chat.id}>
                            <CardHeader>
                                <CardTitle>Chat ID: {chat.id}</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p>
                                    Partecipanti:{" "}
                                    {chat.participantIds?.length
                                        ? chat.participantIds.join(", ")
                                        : "Nessun partecipante"}
                                </p>
                                <Button
                                    className="mt-2"
                                    onClick={() => router.push(`/chat/${chat.id}`)}
                                >
                                    Vai alla chat
                                </Button>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}
        </div>
    )
}
