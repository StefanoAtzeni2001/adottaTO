"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import ExpandedAdoptionCard from "@/components/adoption/ExpandedAdoptionCard" // Assicurati del path corretto

interface AdoptionPost {
    id: number
    name: string
    species: string
    breed: string
    gender: string
    age: number
    color: string
}

interface AdoptionPostDetail {
    id: number
    name: string
    description: string
    publicationDate: string
    species: string
    breed: string
    gender: string
    age: number
    color: string
    ownerId: number
    // Aggiungi opzionali per visualizzare mock nel componente
    location?: string
    vaccinated?: boolean
    chipped?: boolean
    dewormed?: boolean
    ownerName?: string
}

export default function HomePage() {
    const [species, setSpecies] = useState<string | undefined>()
    const [gender, setGender] = useState<string | undefined>()
    const [results, setResults] = useState<AdoptionPost[]>([])
    const [selectedPost, setSelectedPost] = useState<AdoptionPostDetail | null>(null)

    const handleDeleteFilter = () => {
        setSpecies(undefined)
        setGender(undefined)
    }

    const handleSearch = async () => {
        try {
            const params = new URLSearchParams()
            if (species) params.append("species", species)
            if (gender) params.append("gender", gender)

            const res = await fetch(`http://localhost:8090/get-list-filtered?${params.toString()}`)
            if (!res.ok) throw new Error("Errore nella richiesta")
            const data = await res.json()
            setResults(data.content)
        } catch (err) {
            console.error("Errore durante la ricerca:", err)
        }
    }

    const handleCardClick = async (postId: number) => {
        try {
            const res = await fetch(`http://localhost:8090/get-by-id/${postId}`)
            if (!res.ok) throw new Error("Errore nel recupero dettagli")
            const detail = await res.json()
            setSelectedPost(detail)
        } catch (err) {
            console.error("Errore nel caricamento dettagli:", err)
        }
    }

    return (
        <div className="flex flex-col items-center gap-6 p-6">
            {/* Filtro */}
            <div className="flex gap-4">
                <Select onValueChange={(val) => setSpecies(val)}>
                    <SelectTrigger className="w-[180px]">
                        <SelectValue placeholder="Specie" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="Cane">Cane</SelectItem>
                        <SelectItem value="Gatto">Gatto</SelectItem>
                    </SelectContent>
                </Select>

                <Select onValueChange={(val) => setGender(val)}>
                    <SelectTrigger className="w-[180px]">
                        <SelectValue placeholder="Genere" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="M">Maschio</SelectItem>
                        <SelectItem value="F">Femmina</SelectItem>
                    </SelectContent>
                </Select>

                <Button onClick={handleDeleteFilter} variant="outline">
                    Elimina i filtri
                </Button>

                <Button onClick={handleSearch} variant="outline">
                    Ricerca!
                </Button>
            </div>

            {/* Risultati */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-5xl">
                {results.map(post => (
                    <Card key={post.id} className="w-full cursor-pointer" onClick={() => handleCardClick(post.id)}>
                        <CardHeader>
                            <CardTitle>{post.name}</CardTitle>
                            <CardDescription>{post.species} - {post.breed}</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <p><strong>Età:</strong> {post.age} mesi</p>
                            <p><strong>Colore:</strong> {post.color}</p>
                            <p><strong>Sesso:</strong> {post.gender === "M" ? "Maschio" : "Femmina"}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Espansione card */}
            {selectedPost && (
                <ExpandedAdoptionCard post={selectedPost} onClose={() => setSelectedPost(null)} />
            )}
        </div>
    )
}
