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
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
    DialogFooter,
    DialogClose
} from "@/components/ui/dialog"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

interface UserProfile {
    name: string
    surname: string
    email: string
    profilePicture: string
}

export default function Page() {
    const [profile, setProfile] = useState<UserProfile | null>(null)
    const [editedProfile, setEditedProfile] = useState<UserProfile | null>(null)
    const router = useRouter()

    // Stati per il form adozione
    const [petName, setPetName] = useState("")
    const [description, setDescription] = useState("")
    const [species, setSpecies] = useState<string | undefined>()
    const [breed, setBreed] = useState<string | undefined>()
    const [gender, setGender] = useState<string | undefined>()
    const [location, setLocation] = useState<string | undefined>()
    const [color, setColor] = useState<string | undefined>()
    const [age, setAge] = useState("")

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            router.push("/login")
            return
        }

        fetch("http://localhost:8090/profile", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(async res => {
                if (!res.ok) throw new Error("Token non valido")
                return res.json()
            })
            .then(data => setProfile(data))
            .catch(() => {
                localStorage.removeItem("jwt")
                router.push("/login")
            })
    }, [router])

    const handleLogout = () => {
        localStorage.removeItem("jwt")
        router.push("/login")
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!petName || !description || !species || !breed || !gender || !age || !color) {
            alert("Compila tutti i campi obbligatori prima di procedere.");
            return;
        }

        const token = localStorage.getItem("jwt");
        if (!token) {
            alert("Token mancante. Rieffettua il login.");
            return;
        }

        try {
            const res = await fetch("http://localhost:8090/create-adoption-post", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    name: petName,
                    description,
                    species,
                    breed,
                    gender,
                    age: parseInt(age, 10),
                    color,
                }),
            });

            if (res.ok) {
                alert("Annuncio pubblicato con successo!");
                setPetName("");
                setDescription("");
                setSpecies(undefined);
                setBreed(undefined);
                setGender(undefined);
                setColor(undefined);
                setAge("");
            } else {
                const errorData = await res.json();
                alert(`Errore durante la pubblicazione: ${errorData.message || res.statusText}`);
            }
        } catch {
            alert("Errore di rete o del server. Riprova più tardi.");
        }
    };

    if (!profile) return <div>Caricamento...</div>

    return (
        <div className="container py-6">
            <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
                <Avatar className="w-32 h-32">
                    <AvatarImage src={profile.profilePicture ?? "/default-avatar.svg"} />
                    <AvatarFallback>{profile.name.charAt(0)}{profile.surname.charAt(0)}</AvatarFallback>
                </Avatar>

                <div className="flex flex-col items-center md:items-start gap-2">
                    <div className="flex items-center gap-4">
                        <h1 className="text-4xl font-bold">{profile.name} {profile.surname}</h1>

                        <Sheet onOpenChange={(open) => {
                            if (open && profile) {
                                setEditedProfile({ ...profile })
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
                                            const res = await fetch("http://localhost:8090/api/profile/update", {
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

                        <Button variant="destructive" onClick={handleLogout}>
                            Logout
                        </Button>
                    </div>

                    <p className="text-lg text-gray-600">{profile.email}</p>

                    <Dialog>
                        <DialogTrigger asChild>
                            <Button className="bg-red-600 hover:bg-red-700 text-white font-semibold px-6 py-2 mt-2">
                                Pubblica annuncio!
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <form onSubmit={handleSubmit}>
                                <DialogHeader>
                                    <DialogTitle>Pubblica un nuovo annuncio</DialogTitle>
                                    <DialogDescription>
                                        Inserisci tutte le informazioni del tuo animale
                                    </DialogDescription>
                                </DialogHeader>
                                <div className="grid gap-4 py-4">
                                    <div className="grid gap-3">
                                        <Label htmlFor="name">Nome</Label>
                                        <Input id="name" value={petName} onChange={(e) => setPetName(e.target.value)} />
                                    </div>
                                    <div className="grid gap-3">
                                        <Label>Descrizione</Label>
                                        <Textarea placeholder="Inserisci una descrizione" value={description} onChange={(e) => setDescription(e.target.value)} />
                                    </div>
                                    <Select onValueChange={setSpecies} value={species}>
                                        <SelectTrigger className="w-[180px]">
                                            <SelectValue placeholder="Specie" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="Cane">Cane</SelectItem>
                                            <SelectItem value="Gatto">Gatto</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <Select onValueChange={setBreed} value={breed}>
                                        <SelectTrigger className="w-[180px]">
                                            <SelectValue placeholder="Razza" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="Labrador">Labrador</SelectItem>
                                            <SelectItem value="Soriano">Soriano</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <Select onValueChange={setGender} value={gender}>
                                        <SelectTrigger className="w-[180px]">
                                            <SelectValue placeholder="Genere" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="M">Maschio</SelectItem>
                                            <SelectItem value="F">Femmina</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <Select onValueChange={setColor} value={color}>
                                        <SelectTrigger className="w-[180px]">
                                            <SelectValue placeholder="Colore" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="Bianco">Bianco</SelectItem>
                                            <SelectItem value="Arancione">Arancione</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <Select onValueChange={setLocation} value={location}>
                                        <SelectTrigger className="w-[180px]">
                                            <SelectValue placeholder="Città" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="Torino">Torino</SelectItem>
                                            <SelectItem value="Milano">Milano</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <div className="grid gap-3">
                                        <Label htmlFor="age">Età in mesi</Label>
                                        <Input id="age" type="number" value={age} onChange={(e) => setAge(e.target.value)} />
                                    </div>
                                </div>
                                <DialogFooter>
                                    <DialogClose asChild>
                                        <Button variant="outline">Annulla</Button>
                                    </DialogClose>
                                    <Button type="submit">Pubblica</Button>
                                </DialogFooter>
                            </form>
                        </DialogContent>
                    </Dialog>
                </div>
            </div>

            <Separator className="my-8" />
        </div>
    )
}
