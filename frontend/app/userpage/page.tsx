"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
    Sheet,
    SheetClose,
    SheetContent,
    SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
} from "@/components/ui/sheet"

interface UserProfile {
    name: string
    surname: string
    email: string
}

export default function Page() {
    const [profile, setProfile] = useState<UserProfile | null>(null)
    const [editedProfile, setEditedProfile] = useState<UserProfile | null>(null)
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            router.push("/login")
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
                router.push("/login")
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

            <Sheet onOpenChange={(open) => {
                if (open && profile) {
                    setEditedProfile({ ...profile }) // Copia il profilo esistente per modifiche temporanee
                }
            }}>
                <SheetTrigger asChild>
                    <Button variant="outline">Modifica profilo</Button>
                </SheetTrigger>
                <SheetContent>
                    <SheetHeader>
                        <SheetTitle>Modifica il tuo Profilo</SheetTitle>
                        <SheetDescription>
                            Modifica il tuo nome o cognome. Clicca su Salva le modifiche per confermare.
                        </SheetDescription>
                    </SheetHeader>
                    <div className="grid flex-1 auto-rows-min gap-6 px-4">
                        <div className="grid gap-3">
                            <Label htmlFor="sheet-demo-name">Nome</Label>
                            <Input
                                id="sheet-demo-name"
                                value={editedProfile?.name ?? ""}
                                onChange={(e) =>
                                    setEditedProfile(prev => prev ? { ...prev, name: e.target.value } : null)
                                }
                            />
                        </div>
                        <div className="grid gap-3">
                            <Label htmlFor="sheet-demo-username">Cognome</Label>
                            <Input
                                id="sheet-demo-username"
                                value={editedProfile?.surname ?? ""}
                                onChange={(e) =>
                                    setEditedProfile(prev => prev ? { ...prev, surname: e.target.value } : null)
                                }
                            />
                        </div>
                    </div>
                    <SheetFooter>
                        <Button
                            type="button"
                            onClick={async () => {
                                const token = localStorage.getItem("jwt")
                                const res = await fetch("http://localhost:8083/api/profile/update", {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/json",
                                        Authorization: `Bearer ${token}`
                                    },
                                    body: JSON.stringify({
                                        name: editedProfile?.name,
                                        surname: editedProfile?.surname
                                    })
                                })

                                if (res.ok) {
                                    alert("Profilo aggiornato con successo")
                                    if (editedProfile) setProfile(editedProfile)
                                } else {
                                    alert("Errore durante l'aggiornamento del profilo")
                                }
                            }}
                        >
                            Salva le modifiche
                        </Button>
                        <SheetClose asChild>
                            <Button variant="outline">Chiudi</Button>
                        </SheetClose>
                    </SheetFooter>
                </SheetContent>
            </Sheet>

            <h1 className="scroll-m-20 text-center text-4xl font-extrabold tracking-tight text-balance">
                {profile.name} {profile.surname}
            </h1>

            <h4 className="scroll-m-20 text-xl font-semibold tracking-tight">
                {profile.email}
            </h4>

            <div className="flex flex-wrap items-center gap-2 md:flex-row">
                <Button>Pubblica un nuovo annuncio!</Button>
            </div>

            <Separator className="my-4" />
        </div>
    )
}
