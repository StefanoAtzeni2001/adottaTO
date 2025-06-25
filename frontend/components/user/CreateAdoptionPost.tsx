"use client"

import { useState, useEffect } from "react"
import {
    Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle,
    DialogTrigger, DialogFooter, DialogClose
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import {
    catBreeds, dogBreeds, colors, province, genderOptions,
    birdBreeds, turtleBreeds, fishBreeds
} from "@/data/constants"
import { Label } from "@/components/ui/label"
import { ScrollArea } from "@/components/ui/scroll-area"

type CreateAdoptionPostProps = {
    onPostCreated?: () => void;
}

const breedsBySpecies: Record<string, string[]> = {
    Cane: dogBreeds,
    Gatto: catBreeds,
    Uccello: birdBreeds,
    Tartaruga: turtleBreeds,
    Pesce: fishBreeds
}

export default function CreateAdoptionPost({ onPostCreated }: CreateAdoptionPostProps) {
    const [petName, setPetName] = useState("")
    const [description, setDescription] = useState("")
    const [species, setSpecies] = useState<string | undefined>()
    const [breed, setBreed] = useState<string | undefined>()
    const [gender, setGender] = useState<string | undefined>()
    const [location, setLocation] = useState<string | undefined>()
    const [color, setColor] = useState<string | undefined>()
    const [age, setAge] = useState("")
    const [imageFile, setImageFile] = useState<File | null>(null)
    const [breeds, setBreeds] = useState<string[]>([])

    useEffect(() => {
        if (species && breedsBySpecies[species]) {
            setBreeds(breedsBySpecies[species])
        } else {
            setBreeds([])
        }
        setBreed(undefined)
    }, [species])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!petName || !description || !species || !breed || !gender || !age || !color || !location) {
            alert("Compila tutti i campi.")
            return
        }

        const token = localStorage.getItem("jwt")
        if (!token) return alert("Effettua il login.")

        const post = {
            name: petName,
            description,
            species,
            breed,
            gender,
            age: parseInt(age, 10),
            color,
            location
        }

        const formData = new FormData()
        formData.append("post", new Blob([JSON.stringify(post)], { type: "application/json" }))
        if (imageFile) {
            formData.append("image", imageFile)
        }

        const res = await fetch("http://localhost:8090/create-adoption-post", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            body: formData
        })

        if (res.ok) {
            alert("Annuncio pubblicato!")
            setPetName("")
            setDescription("")
            setSpecies(undefined)
            setBreed(undefined)
            setGender(undefined)
            setColor(undefined)
            setLocation(undefined)
            setAge("")
            setImageFile(null)

            if (onPostCreated) {
                onPostCreated()
            }
        } else {
            const err = await res.json()
            alert(`Errore: ${err.message || res.statusText}`)
        }
    }

    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button className="bg-red-600 text-white">Pubblica annuncio!</Button>
            </DialogTrigger>
            <DialogContent className="max-w-3xl">
                <form onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle>Nuovo annuncio</DialogTitle>
                        <DialogDescription>Compila i dati del tuo animale</DialogDescription>
                    </DialogHeader>
                    <ScrollArea className="h-[400px] rounded-md border p-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 py-6">
                            <div className="flex flex-col gap-2">
                                <Label>Nome</Label>
                                <Input value={petName} onChange={(e) => setPetName(e.target.value)} placeholder="Nome" />
                            </div>

                            <div className="flex flex-col gap-2">
                                <Label>Specie</Label>
                                <Select value={species} onValueChange={setSpecies}>
                                    <SelectTrigger><SelectValue placeholder="Specie" /></SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="Cane">Cane</SelectItem>
                                        <SelectItem value="Gatto">Gatto</SelectItem>
                                        <SelectItem value="Uccello">Uccello</SelectItem>
                                        <SelectItem value="Tartaruga">Tartaruga</SelectItem>
                                        <SelectItem value="Pesce">Pesce</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex flex-col gap-2">
                                <Label>Razza</Label>
                                <Select value={breed} onValueChange={setBreed} disabled={breeds.length === 0}>
                                    <SelectTrigger><SelectValue placeholder="Razza" /></SelectTrigger>
                                    <SelectContent>
                                        {breeds.map(b => (
                                            <SelectItem key={b} value={b}>{b}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex flex-col gap-2">
                                <Label>Genere</Label>
                                <Select value={gender} onValueChange={setGender}>
                                    <SelectTrigger><SelectValue placeholder="Genere" /></SelectTrigger>
                                    <SelectContent>
                                        {genderOptions.map(opt => (
                                            <SelectItem key={opt.value} value={opt.value}>{opt.label}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex flex-col gap-2">
                                <Label>Colore</Label>
                                <Select value={color} onValueChange={setColor}>
                                    <SelectTrigger><SelectValue placeholder="Colore" /></SelectTrigger>
                                    <SelectContent>
                                        {colors.map(c => (
                                            <SelectItem key={c} value={c}>{c}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex flex-col gap-2">
                                <Label>Provincia</Label>
                                <Select value={location} onValueChange={setLocation}>
                                    <SelectTrigger><SelectValue placeholder="Provincia" /></SelectTrigger>
                                    <SelectContent>
                                        {province.map(p => (
                                            <SelectItem key={p} value={p}>{p}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex flex-col gap-2">
                                <Label>Età (in mesi)</Label>
                                <Input
                                    type="number"
                                    value={age}
                                    onChange={(e) => setAge(e.target.value)}
                                    placeholder="Età in mesi"
                                />
                            </div>

                            <div className="md:col-span-2 flex flex-col gap-2">
                                <Label>Descrizione</Label>
                                <Textarea
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                    placeholder="Inserisci una descrizione"
                                    rows={4}
                                />
                            </div>

                            <div className="md:col-span-2 flex flex-col gap-2">
                                <Label>Immagine</Label>
                                <Input
                                    type="file"
                                    accept="image/*"
                                    onChange={(e) => {
                                        if (e.target.files?.[0]) {
                                            setImageFile(e.target.files[0])
                                        }
                                    }}
                                />
                            </div>
                        </div>
                    </ScrollArea>

                    <DialogFooter>
                        <DialogClose asChild>
                            <Button variant="outline">Annulla</Button>
                        </DialogClose>
                        <Button type="submit">Pubblica</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
