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
import { Separator } from "@/components/ui/separator"

export default function Page() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")

    const router = useRouter()

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault()

        try {
            const res = await fetch("http://localhost:8083/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            })

            if (res.ok) {
                const data = await res.json()
                console.log("Login OK. Token:", data.token)
                router.push("/userpage")
            } else {
                console.error("Login fallito")
            }
        } catch (err) {
            console.error("Errore di rete:", err)
        }
    }

    const handleGoogleLogin = () => {
        window.location.href = "http://localhost:8083/oauth2/authorization/google"
    }

    return (
        <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
            <div className="w-full max-w-sm">
                <CardHeader>
                    <div className="flex flex-col items-center gap-2 text-center">
                        <h1 className="text-2xl font-bold">Accedi al tuo profilo</h1>
                        <p className="text-muted-foreground text-sm text-balance">
                            Inserisci la tua email e la password qui sotto per accedere al tuo profilo
                        </p>
                    </div>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleLogin}>
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
                        </div>
                    </form>
                </CardContent>
                <Separator  className="my-4" />
                <CardFooter className="flex-col gap-2">
                    <Button type="submit" className="w-full" onClick={handleLogin}>
                        Login
                    </Button>
                    <Button variant="outline" className="w-full" onClick={handleGoogleLogin}>
                        Login con Google
                    </Button>

                    <div className="text-center text-sm">
                        Non hai un account?{" "}
                        <a href="/signUp" className="underline underline-offset-4">
                            Registrati
                        </a>
                    </div>
                </CardFooter>
            </div>
        </div>
    )

}
