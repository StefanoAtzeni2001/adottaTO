"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import ExpandedAdoptionCard from "@/components/adoption/ExpandedAdoptionCard"
import SearchFilters from "@/components/adoption/SearchFilters"
import Image from "next/image"

interface AdoptionPost {
    id: number
    name: string
    species: string
    breed: string
    gender: string
    age: number
    color: string
    location: string
    imageBase64: string
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
    location: string
    ownerId: number
    ownerName: string
    imageBase64: string
}

interface Filters {
    species?: string[]
    breed?: string[]
    gender?: string     // gender è stringa singola
    color?: string[]
    location?: string[]
    minAge?: string
    maxAge?: string
    activeOnly?: boolean
}

export default function HomePage() {
    const [results, setResults] = useState<AdoptionPost[]>([])
    const [selectedPost, setSelectedPost] = useState<AdoptionPostDetail | null>(null)
    const [lastFilters, setLastFilters] = useState<Filters | null>(null)

    const handleSearch = async (filters: Filters) => {
        setLastFilters(filters)

        try {
            const params = new URLSearchParams()

            Object.entries(filters).forEach(([key, value]) => {
                if (!value) return
                // Se il valore è un array (species, breed, etc.), aggiungo tutti i valori
                if (Array.isArray(value)) {
                    value.forEach(v => params.append(key, v))
                } else {
                    // Altrimenti aggiungo il valore singolo (gender e minAge, maxAge)
                    params.append(key, value)
                }
            })
            params.append("activeOnly", "True")
            const res = await fetch(`http://localhost:8090/get-list-filtered?${params.toString()}`)
            if (!res.ok) throw new Error("Errore nella richiesta")
            const data = await res.json()
            setResults(data.content)
            console.log(results)
        } catch (err) {
            console.error("Errore durante la ricerca:", err)
        }
    }

    const handleSaveSearch = async () => {
        const token = localStorage.getItem("jwt")

        if (!lastFilters) {
            alert("Effettua prima una ricerca per poterla salvare.")
            return
        }

        const dto = {
            species: lastFilters.species || [],
            breed: lastFilters.breed || [],
            gender: lastFilters.gender || "",  // stringa singola qui
            color: lastFilters.color || [],
            location: lastFilters.location || [],
            minAge: lastFilters.minAge ? parseInt(lastFilters.minAge) : null,
            maxAge: lastFilters.maxAge ? parseInt(lastFilters.maxAge) : null,
            activeOnly: true
        }

        try {
            const res = await fetch("http://localhost:8090/save-search", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(dto)
            })

            if (!res.ok) throw new Error("Errore nel salvataggio della ricerca")
            alert("Ricerca salvata con successo!")
        } catch (err) {
            console.error("Errore durante il salvataggio della ricerca:", err)
            alert("Errore durante il salvataggio della ricerca.")
        }
    }

    const handleCardClick = async (postId: number) => {
        try {
            const res = await fetch(`http://localhost:8090/get-by-id/${postId}`)
            if (!res.ok) throw new Error("Errore nel recupero dettagli")
            const detail = await res.json()
            setSelectedPost(detail)
            console.log(selectedPost)
        } catch (err) {
            console.error("Errore nel caricamento dettagli:", err)
        }
    }

    return (
        <div className="flex flex-col items-center gap-6 p-6">
            <SearchFilters
                onSearchAction={handleSearch}
                onSaveSearch={handleSaveSearch}
                canSaveSearch={!!lastFilters}
            />

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-6xl mt-6">
                {results.map(post => (
                    <Card key={post.id} className="cursor-pointer" onClick={() => handleCardClick(post.id)}>
                        <CardHeader>
                            <CardTitle>{post.name}</CardTitle>
                            <CardDescription>{post.species} - {post.breed}</CardDescription>
                        </CardHeader>
                        <div className=" relative w-[95%] h-48 overflow-hidden rounded-md mx-auto">
                            <Image
                                src={post.imageBase64 ? `data:image/jpeg;base64,${post.imageBase64}` : "/no_content.jpg"}
                                alt={`Immagine di ${post.name}`}
                                fill
                                className="object-cover"
                                unoptimized
                            />
                        </div>
                        <CardContent >
                            <p><strong>Provincia:</strong> {post.location}</p>
                            <p><strong>Età:</strong> {post.age} mesi</p>
                            <p><strong>Colore:</strong> {post.color}</p>
                            <p><strong>Sesso:</strong> {post.gender === "M" ? "Maschio" : "Femmina"}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {selectedPost && (
                <ExpandedAdoptionCard post={selectedPost} onClose={() => setSelectedPost(null)} />
            )}
        </div>

    )
}
