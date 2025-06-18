"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import {
    CardContent,
    CardFooter,
    CardHeader,
} from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {Separator} from "@/components/ui/separator";

export default function Page() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [name, setName] = useState("")
    const [surname, setSurname] = useState("")

    const router = useRouter()

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault()

        try {
            const res = await fetch("http://localhost:8090/api/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password, name, surname })
            })

            if (res.ok) {
                console.log("Registrazione completata")
                router.push("/login")
            } else if (res.status === 409) {
                console.error("Email già registrata")
            } else {
                console.error("Errore durante la registrazione")
            }
        } catch (err) {
            console.error("Errore di rete:", err)
        }
    }


    return (
        <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
            <div className="w-full max-w-sm">
                <CardHeader>
                    <div className="flex flex-col items-center gap-2 text-center">
                        <h1 className="text-2xl font-bold">Crea un nuovo profilo</h1>
                        <p className="text-muted-foreground text-sm text-balance">
                            Inserisci le credenziali richieste qui sotto per creare il tuo nuovo profilo
                        </p>
                    </div>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleRegister}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-2">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    placeholder="m@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="grid gap-2">
                                <div className="flex items-center">
                                    <Label htmlFor="password">Password</Label>
                                </div>
                                <Input
                                    id="password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="grid gap-2">
                                <div className="flex items-center">
                                    <Label htmlFor="name">Name</Label>
                                </div>
                                <Input
                                    id="name"
                                    type="text"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="grid gap-2">
                                <div className="flex items-center">
                                    <Label htmlFor="surname">Surname</Label>
                                </div>
                                <Input
                                    id="surname"
                                    type="text"
                                    value={surname}
                                    onChange={(e) => setSurname(e.target.value)}
                                    required
                                />
                            </div>
                        </div>
                    </form>
                </CardContent>
                <Separator  className="my-4" />
                <CardFooter className="flex-col gap-2">
                    <Button type="submit" className="w-full" onClick={handleRegister}>
                        Registrati
                    </Button>

                    <div className="text-center text-sm">
                        Hai un account?{" "}
                        <a href="/login" className="underline underline-offset-4">
                            Accedi
                        </a>
                    </div>
                </CardFooter>
            </div>
        </div>
    )

}
