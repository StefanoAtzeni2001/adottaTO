"use client"

import { useState } from "react"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogFooter
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import {
    Select,
    SelectTrigger,
    SelectContent,
    SelectItem,
    SelectValue,
} from "@/components/ui/select"

import { catBreeds, dogBreeds, colors, province } from "@/data/constants"

interface Props {
    post: {
        id: number
        name: string
        description: string
        species: string
        breed: string
        gender: string
        age: number
        color: string
        location: string
        imageBase64?: string
    }
    onClose: () => void
    onUpdated: () => void
}

export default function EditAdoptionPost({ post, onClose, onUpdated }: Props) {
    const [formData, setFormData] = useState({
        name: post.name,
        description: post.description,
        species: post.species,
        breed: post.breed,
        gender: post.gender,
        age: post.age,
        color: post.color,
        location: post.location,
        imageBase64: post.imageBase64 || "",
    })

    const originalSpecies = post.species
    const breedOptions = formData.species === "Cane" ? dogBreeds : catBreeds
    const speciesChanged = formData.species !== originalSpecies
    const breedValid = breedOptions.includes(formData.breed)
    const canSubmit = !speciesChanged || (speciesChanged && breedValid)

    const handleChange = (field: string, value: string | number) => {
        setFormData(prev => {
            if (field === "species" && value !== prev.species) {
                return { ...prev, species: value as string, breed: "" }
            }
            return { ...prev, [field]: value }
        })
    }

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (file) {
            const reader = new FileReader()
            reader.onloadend = () => {
                setFormData(prev => ({
                    ...prev,
                    imageBase64: reader.result as string,
                }))
            }
            reader.readAsDataURL(file)
        }
    }

    const handleSubmit = async () => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            alert("Devi essere loggato per modificare un annuncio.")
            return
        }

        try {
            const formDataToSend = new FormData()

            // Estraggo imageBase64 e uso il resto come postPayload
            const { imageBase64, ...postPayload } = formData

            formDataToSend.append("post", new Blob([JSON.stringify(postPayload)], { type: "application/json" }))

            if (imageBase64) {
                // formData.imageBase64 è una data URL, es: "data:image/png;base64,...."
                // La converto in Blob
                const res = await fetch(imageBase64)
                const blob = await res.blob()

                formDataToSend.append("image", blob, "image.png")
            }

            const res = await fetch(`http://localhost:8090/update-by-id/${post.id}`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                body: formDataToSend,
            })

            if (!res.ok) throw new Error("Errore durante l'aggiornamento")
            alert("Annuncio aggiornato con successo.")
            onClose()
            onUpdated()
        } catch (err) {
            console.error(err)
            alert("Errore durante l'aggiornamento.")
        }
    }

    return (
        <Dialog open onOpenChange={onClose}>
            <DialogContent className="max-w-2xl">
                <DialogHeader>
                    <DialogTitle>Modifica Annuncio</DialogTitle>
                </DialogHeader>

                <div className="grid gap-4">
                    <Input
                        placeholder="Nome"
                        value={formData.name}
                        onChange={e => handleChange("name", e.target.value)}
                    />
                    <Textarea
                        placeholder="Descrizione"
                        value={formData.description}
                        onChange={e => handleChange("description", e.target.value)}
                    />

                    <Select
                        value={formData.species}
                        onValueChange={val => handleChange("species", val)}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Specie" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="Cane">Cane</SelectItem>
                            <SelectItem value="Gatto">Gatto</SelectItem>
                        </SelectContent>
                    </Select>

                    <Select
                        value={formData.breed}
                        onValueChange={val => handleChange("breed", val)}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Razza" />
                        </SelectTrigger>
                        <SelectContent>
                            {breedOptions.map(b => (
                                <SelectItem key={b} value={b}>
                                    {b}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    <Select
                        value={formData.gender}
                        onValueChange={val => handleChange("gender", val)}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Sesso" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="M">Maschio</SelectItem>
                            <SelectItem value="F">Femmina</SelectItem>
                        </SelectContent>
                    </Select>

                    <Input
                        type="number"
                        placeholder="Età (mesi)"
                        value={formData.age}
                        onChange={e => handleChange("age", parseInt(e.target.value))}
                    />

                    <Select
                        value={formData.color}
                        onValueChange={val => handleChange("color", val)}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Colore" />
                        </SelectTrigger>
                        <SelectContent>
                            {colors.map(c => (
                                <SelectItem key={c} value={c}>
                                    {c}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    <Select
                        value={formData.location}
                        onValueChange={val => handleChange("location", val)}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Provincia" />
                        </SelectTrigger>
                        <SelectContent>
                            {province.map(p => (
                                <SelectItem key={p} value={p}>
                                    {p}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    <div className="space-y-2">
                        <label className="block text-sm font-medium">Immagine</label>
                        {formData.imageBase64 && (
                            <img
                                src={formData.imageBase64}
                                alt="Preview"
                                className="h-32 object-cover rounded border"
                            />
                        )}
                        <Input
                            type="file"
                            accept="image/*"
                            onChange={handleImageChange}
                        />
                    </div>
                </div>

                {speciesChanged && !breedValid && (
                    <p className="text-sm text-red-500 mt-2">
                        Hai cambiato la specie. Devi selezionare una nuova razza compatibile.
                    </p>
                )}

                <DialogFooter className="mt-6">
                    <Button
                        onClick={handleSubmit}
                        className="bg-yellow-500 hover:bg-yellow-600"
                        disabled={!canSubmit}
                    >
                        Salva Modifiche
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}
