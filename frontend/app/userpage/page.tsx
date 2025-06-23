"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import ExpandedAdoptionCard from "@/components/user/ExpandedAdoptionCardUser"
import EditProfile from "@/components/user/EditProfile"
import PostAdoption from "@/components/user/CreateAdoptionPost"
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

export default function UserPage() {
    const [profile, setProfile] = useState<UserProfile | null>(null)
    const [posts, setPosts] = useState<AdoptionPostDetailDto[]>([])
    const [selectedPost, setSelectedPost] = useState<AdoptionPostDetailDto | null>(null)
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem("jwt")
        if (!token) {
            router.push("/login")
            return
        }

        fetch("http://localhost:8090/profile", {
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

        // Carica gli ID dei post, poi recupera i dettagli
        fetch("http://localhost:8090/get-my-owned-posts", {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.ok ? res.json() : Promise.reject("Errore nella richiesta"))
            .then(async (summaryPosts) => {
                const details = await Promise.all(summaryPosts.content.map((post: { id: number }) =>
                    fetch(`http://localhost:8090/get-by-id/${post.id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    }).then(res => res.json())
                ))
                setPosts(details)
            })
            .catch(err => console.error("Errore caricamento annunci:", err))
    }, [router])

    const handleProfileUpdate = async (name: string, surname: string,imageFile?: File) => {
        const token = localStorage.getItem("jwt")
        const formData = new FormData()
        formData.append("request", new Blob([JSON.stringify({ name, surname })], { type: "application/json" }))
        if (imageFile) {
            formData.append("image", imageFile)
        }
        const res = await fetch("http://localhost:8090/api/profile/update", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            },
            body: formData
        })

        if (res.ok) {
            alert("Profilo aggiornato con successo")
            // Re-fetch the updated profile from backend
            const updatedProfile = await fetch("http://localhost:8090/profile", {
                headers: { Authorization: `Bearer ${token}` }
            }).then(res => res.json())

            setProfile(updatedProfile)
        } else {
            alert("Errore durante l'aggiornamento del profilo")
        }
    }

    const handleLogout = () => {
        localStorage.removeItem("jwt")
        router.push("/login")
    }

    const handleCardClick = async (postId: number) => {
        const token = localStorage.getItem("jwt")
        try {
            const res = await fetch(`http://localhost:8090/get-by-id/${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            if (!res.ok) throw new Error("Errore nel recupero dettagli")
            const detail = await res.json()
            setSelectedPost(detail)
        } catch (err) {
            console.error("Errore nel caricamento dettagli:", err)
        }
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
                    <PostAdoption />
                </div>
            </div>

            <Separator className="my-8" />

            <h1 className="text-4xl font-bold mb-4">I miei annunci:</h1>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-6xl">
                {posts.map(post => (
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
