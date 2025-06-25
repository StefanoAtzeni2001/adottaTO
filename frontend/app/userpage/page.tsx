"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle
} from "@/components/ui/card"
import ExpandedAdoptionCard from "@/components/user/ExpandedAdoptionCardUser"
import EditProfile from "@/components/user/EditProfile"
import PostAdoption from "@/components/user/CreateAdoptionPost"
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger
} from "@/components/ui/tabs"
import Image from "next/image";

interface UserProfile {
    name: string
    surname: string
    email: string
    profilePicture: string
}

interface AdoptionPostDetailDto {
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
    active: boolean
    adopterId: number | null
    imageBase64: string
}

interface AdoptionPostSavedSearchDto {
    id: number
    species: string[]
    breed: string[]
    gender: string
    minAge: number
    maxAge: number
    color: string[]
    location: string[]
}

export default function UserPage() {
    const [profile, setProfile] = useState<UserProfile | null>(null)
    const [posts, setPosts] = useState<AdoptionPostDetailDto[]>([])
    const [selectedPost, setSelectedPost] = useState<AdoptionPostDetailDto | null>(null)
    const [savedSearches, setSavedSearches] = useState<AdoptionPostSavedSearchDto[]>([])
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        const userId = localStorage.getItem("userId")
        if (!token) {
            router.push("/login")
            return
        }

        fetch(`http://localhost:8090/user/my-profile`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(async res => {
                if (!res.ok) throw new Error("Token non valido")
                return res.json()
            })
            .then(setProfile)
            .catch(() => {
                localStorage.removeItem("jwt")
                router.push("/login")
            })

        if (userId) {
            fetch("http://localhost:8090/search/my/saved", {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "User-Id": userId
                }
            })
                .then(res => res.ok ? res.json() : Promise.reject("Errore nelle ricerche salvate"))
                .then(setSavedSearches)
                .catch(err => console.error("Errore nel caricamento delle ricerche salvate:", err))
        }

        fetchUserPosts()
    }, [router])

    const handleProfileUpdate = async (name: string, surname: string,imageFile?: File) => {
        const token = localStorage.getItem("jwt")
        const formData = new FormData()
        formData.append("request", new Blob([JSON.stringify({ name, surname })], { type: "application/json" }))
        if (imageFile) {
            formData.append("image", imageFile)
        }
        const res = await fetch("http://localhost:8090/user/my-profile/update", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            body: formData
        })

        if (res.ok) {
            alert("Profilo aggiornato con successo")
            // Re-fetch the updated profile from backend
            const updatedProfile = await fetch("http://localhost:8090/user/my-profile", {
                headers: { Authorization: `Bearer ${token}` }
            }).then(res => res.json())

            setProfile(updatedProfile)
        } else {
            alert("Errore durante l'aggiornamento del profilo")
        }
    }

    const handleLogout = () => {
        localStorage.removeItem("jwt")
        localStorage.removeItem("userId")
        router.push("/login")
    }

    const handleGoChat = () => {
        router.push("/chat")
    }

    const handleCardClick = async (postId: number) => {
        const token = localStorage.getItem("jwt")
        try {
            const res = await fetch(`http://localhost:8090/adoption/get/post/${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            if (!res.ok) throw new Error("Errore nel recupero dettagli")
            const detail = await res.json()
            setSelectedPost(detail)
        } catch (err) {
            console.error("Errore nel caricamento dettagli:", err)
        }
    }

    const handleDeleteSearch = async (searchId: number) => {
        const token = localStorage.getItem("jwt")
        const userId = localStorage.getItem("userId")
        if (!token || !userId) return

        const confirmed = confirm("Sei sicuro di voler eliminare questa ricerca salvata?")
        if (!confirmed) return

        try {
            const res = await fetch(`http://localhost:8090/search/delete/${searchId}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "User-Id": userId
                }
            })
            if (res.ok) {
                setSavedSearches(prev => prev.filter(s => s.id !== searchId))
                fetchUserPosts()
                setSelectedPost(null)
            } else {
                alert("Errore durante l'eliminazione della ricerca")
            }
        } catch (err) {
            console.error("Errore nella cancellazione:", err)
        }
    }

    const fetchUserPosts = () => {
        const token = localStorage.getItem("jwt")
        if (!token) return

        fetch("http://localhost:8090/adoption/my/owned", {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.ok ? res.json() : Promise.reject("Errore nella richiesta"))
            .then(async (summaryPosts) => {
                const details = await Promise.all(summaryPosts.content.map((post: { id: number }) =>
                    fetch(`http://localhost:8090/adoption/get/post/${post.id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    }).then(res => res.json())
                ))
                setPosts(details)
            })
            .catch(err => console.error("Errore caricamento annunci:", err))
    }


    if (!profile) return <div>Caricamento...</div>

    return (
        <div className="container py-6">
            <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
                <Avatar className="w-32 h-32">
                    <AvatarImage
                        src={profile.profilePicture
                            ? `data:image/jpeg;base64,${profile.profilePicture}`
                            : "/default-avatar.svg"}
                    />
                    <AvatarFallback>{profile.name[0]}{profile.surname[0]}</AvatarFallback>
                </Avatar>

                <div className="flex flex-col items-center md:items-start gap-2">
                    <div className="flex items-center gap-4">
                        <h1 className="text-4xl font-bold">{profile.name} {profile.surname}</h1>
                        <EditProfile profile={profile} onUpdateAction={handleProfileUpdate} />
                        <Button variant="destructive" onClick={handleLogout}>Logout</Button>
                    </div>
                    <p className="text-lg text-gray-600">{profile.email}</p>
                    <PostAdoption onPostCreated={fetchUserPosts} />
                    <Button
                        onClick={handleGoChat}
                        className="bg-red-600 text-white">
                        Le mie chat
                    </Button>
                </div>
            </div>

            <Separator className="my-8" />

            <Tabs defaultValue="Annunci" className="w-full">
                <TabsList>
                    <TabsTrigger value="Annunci">Annunci</TabsTrigger>
                    <TabsTrigger value="RicercaSalvata">Ricerca Salvata</TabsTrigger>
                </TabsList>

                <TabsContent value="Annunci">
                    <h1 className="text-4xl font-bold mb-4">I miei annunci:</h1>

                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-6xl">
                        {posts.map(post => (
                            <Card
                                key={post.id}
                                className={`cursor-pointer transition-opacity ${!post.active ? "opacity-50 grayscale" : ""}`}
                                onClick={() => handleCardClick(post.id)}
                            >
                                <CardHeader>
                                    <CardTitle>{post.name}</CardTitle>
                                    <CardDescription>
                                        {post.species} - {post.breed}
                                        {!post.active && <span className="text-red-500 ml-2">(Adottato)</span>}
                                    </CardDescription>
                                </CardHeader>

                                <div className="relative w-[95%] h-48 overflow-hidden rounded-md mx-auto">
                                    <Image
                                        src={post.imageBase64 ? `data:image/jpeg;base64,${post.imageBase64}` : "/no_content.jpg"}
                                        alt={`Immagine di ${post.name}`}
                                        fill
                                        className="object-cover"
                                        unoptimized
                                    />
                                </div>

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
                        <ExpandedAdoptionCard onPostCreated={fetchUserPosts} post={selectedPost} onClose={() => setSelectedPost(null)} />
                    )}
                </TabsContent>


                <TabsContent value="RicercaSalvata">
                    <h1 className="text-4xl font-bold mb-4">Le mie ricerche salvate:</h1>
                    <ul className="space-y-4">
                        {savedSearches.map(search => (
                            <li
                                key={search.id}
                                className="border rounded p-4 hover:shadow w-full flex justify-between items-start gap-4"
                            >
                                <div>
                                    <h2 className="font-semibold text-lg mb-2">Ricerca #{search.id}</h2>
                                    <div className="flex flex-nowrap gap-6 overflow-x-auto text-sm text-gray-700">
                                        <span><strong>Specie:</strong> {search.species.join(", ") || "Nessuna"}</span>
                                        <span><strong>Razza:</strong> {search.breed.join(", ") || "Nessuna"}</span>
                                        <span><strong>Età:</strong> {search.minAge} - {search.maxAge} mesi</span>
                                        <span><strong>Sesso:</strong> {search.gender === "M" ? "Maschio" : search.gender === "F" ? "Femmina" : "Indifferente"}</span>
                                        <span><strong>Colore:</strong> {search.color.join(", ") || "Nessuno"}</span>
                                        <span><strong>Provincia:</strong> {search.location.join(", ") || "Nessuna"}</span>
                                    </div>
                                </div>

                                <Button
                                    variant="destructive"
                                    onClick={() => handleDeleteSearch(search.id)}
                                >
                                    Elimina
                                </Button>
                            </li>
                        ))}
                    </ul>
                </TabsContent>
            </Tabs>
        </div>
    )
}
