"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import ExpandedAdoptionCard from "@/components/adoption/ExpandedAdoptionCard"
import SearchFilters from "@/components/adoption/SearchFilters"

interface AdoptionPost {
    id: number
    name: string
    species: string
    breed: string
    gender: string
    age: number
    color: string
    location: string
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
}

// Tipi dei filtri con array di stringhe per quelli multipli
interface Filters {
    species?: string[]
    breed?: string[]
    gender?: string[]
    color?: string[]
    location?: string[]
    minAge?: string
    maxAge?: string
}

export default function HomePage() {
    const [results, setResults] = useState<AdoptionPost[]>([])
    const [selectedPost, setSelectedPost] = useState<AdoptionPostDetail | null>(null)

    const handleSearch = async (filters: Filters) => {
        try {
            const params = new URLSearchParams()

            Object.entries(filters).forEach(([key, value]) => {
                if (!value) return

                if (Array.isArray(value)) {
                    value.forEach(v => params.append(key, v))
                } else {
                    params.append(key, value)
                }
            })

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
            <SearchFilters onSearchAction={handleSearch} />

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-6xl mt-6">
                {results.map(post => (
                    <Card key={post.id} className="cursor-pointer" onClick={() => handleCardClick(post.id)}>
                        <CardHeader>
                            <CardTitle>{post.name}</CardTitle>
                            <CardDescription>{post.species} - {post.breed}</CardDescription>
                        </CardHeader>
                        <CardContent>
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
