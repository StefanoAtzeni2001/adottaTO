import { X } from "lucide-react"
import Image from "next/image"
import {useEffect, useState} from "react";

interface ExpandedAdoptionCardProps {
    post: {
        id: number
        name: string
        description: string
        species: string
        breed: string
        gender: string
        age: number
        color: string
        ownerId: number
        publicationDate: string
        location: string
        ownerName?: string
    }
    onClose: () => void
}

interface UserProfile {
    name: string
    surname: string
    email: string
    profilePicture?: string
}

export default function ExpandedAdoptionCard({ post, onClose }: ExpandedAdoptionCardProps) {
    const [userProfile, setUserProfile] = useState<UserProfile | null>(null)

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const res = await fetch(`http://localhost:8090/api/profile/${post.ownerId}`)
                if (!res.ok) throw new Error("Errore nella fetch")
                const data = await res.json()
                setUserProfile(data)
            } catch (err) {
                console.error("Errore caricamento profilo:", err)
            }
        }
        fetchUserProfile()
    }, [post.ownerId])

    return (
        <div className="fixed inset-0 backdrop-blur-sm bg-white/30 flex justify-center items-center z-50">
            <div className="relative bg-white rounded-2xl max-w-4xl w-full p-6">
                {/* Bottone chiudi */}
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-red-500 hover:text-red-700"
                >
                    <X size={32} />
                </button>

                {/* Immagine placeholder */}
                <div className="flex justify-center mb-4">
                    <Image
                        src="/default-pet.jpg"
                        alt="Immagine animale"
                        width={500}
                        height={300}
                        className="rounded-xl object-cover"
                    />
                </div>

                {/* Titolo e descrizione */}
                <h1 className="text-4xl font-bold">{post.name}</h1>
                <p className="text-lg font-semibold mt-2">Descrizione</p>
                <p className="text-gray-700 mb-4">
                    {post.description || "Nessuna descrizione disponibile."}
                </p>

                {/* Info */}
                <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
                    <p><strong>Specie:</strong> {post.species}</p>
                    <p><strong>Razza:</strong> {post.breed}</p>
                    <p><strong>Colore:</strong> {post.color}</p>
                    <p><strong>Sesso:</strong> {post.gender === "M" ? "Maschio" : "Femmina"}</p>
                    <p><strong>Età:</strong> {post.age} mesi</p>
                    <p><strong>Data Pubblicazione:</strong> {new Date(post.publicationDate).toLocaleDateString()}</p>
                    <p><strong>Località:</strong> {post.location}</p>
                </div>

                {/* Proprietario + bottone azione */}
                <div className="flex items-center justify-between mt-6">
                    <div className="flex items-center gap-2">
                        <Image
                            src={userProfile?.profilePicture || "/default-avatar.png"}
                            alt="Proprietario"
                            width={40}
                            height={40}
                            className="rounded-full"
                        />
                        <span className="font-bold text-lg">
                            {userProfile ? `${userProfile.name} ${userProfile.surname}` : `Proprietario #${post.ownerId}`}
                        </span>
                    </div>
                    <button className="bg-red-600 text-white px-6 py-2 rounded-full hover:bg-red-700">
                        Manda Proposta
                    </button>
                </div>
            </div>
        </div>
    )
}
