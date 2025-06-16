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
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"

interface AdoptionPost {
    id: number
    name: string
    species: string
    breed: string
    gender: string
    age: number
    color: string
}

export default function HomePage() {
    const [species, setSpecies] = useState("")
    const [gender, setGender] = useState("")
    const [results, setResults] = useState<AdoptionPost[]>([])

    const handleSearch = async () => {
        try {
            const params = new URLSearchParams()
            if (species) params.append("species", species)
            if (gender) params.append("gender", gender)

            const res = await fetch(`http://localhost:8081/adoptions/get-list-filtered?${params.toString()}`)
            if (!res.ok) throw new Error("Errore nella richiesta")
            const data = await res.json()
            setResults(data.content) // `content` viene da Page<AdoptionPostSummaryDto>
        } catch (err) {
            console.error("Errore durante la ricerca:", err)
        }
    }

    return (
        <div className="flex flex-col items-center gap-6 p-6">
            {/* Filtro */}
            <div className="flex gap-4">
                <Select onValueChange={setSpecies}>
                    <SelectTrigger className="w-[180px]">
                        <SelectValue placeholder="Specie" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="Cane">Cane</SelectItem>
                        <SelectItem value="Gatto">Gatto</SelectItem>
                    </SelectContent>
                </Select>

                <Select onValueChange={setGender}>
                    <SelectTrigger className="w-[180px]">
                        <SelectValue placeholder="Genere" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="M">Maschio</SelectItem>
                        <SelectItem value="F">Femmina</SelectItem>
                    </SelectContent>
                </Select>

                <Button onClick={handleSearch} variant="outline">
                    Ricerca!
                </Button>
            </div>

            {/* Risultati */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-5xl">
                {results.map(post => (
                    <Card key={post.id} className="w-full">
                        <CardHeader>
                            <CardTitle>{post.name}</CardTitle>
                            <CardDescription>{post.species} - {post.breed}</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <p><strong>Età:</strong> {post.age} mesi</p>
                            <p><strong>Colore:</strong> {post.color}</p>
                            <p><strong>Sesso:</strong> {post.gender === "M" ? "Maschio" : "Femmina"}</p>
                        </CardContent>
                        <CardFooter>
                            <Button variant="outline">Dettagli</Button>
                        </CardFooter>
                    </Card>
                ))}
            </div>
        </div>
    )
}
