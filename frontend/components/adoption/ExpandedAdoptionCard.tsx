import { X } from "lucide-react"
import Image from "next/image"
import { useEffect, useState } from "react"

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
    profilePicture?: string | null
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

    const handleSendProposal = async () => {

        const token = localStorage.getItem("jwt")
        const userId = localStorage.getItem("userId")

        try {
            const res = await fetch("http://localhost:8090/chat/send", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    senderId: userId,
                    receiverId: post.ownerId,
                    adoptionPostId: post.id,
                    message: "Ciao! Mi piacerebbe adottare " + post.name + ".",
                    type: "text"
                })
            })

            if (!res.ok) throw new Error("Errore durante l'invio della proposta")

            alert("Proposta inviata con successo!")
        } catch (err) {
            console.error("Errore invio proposta:", err)
            alert("Errore durante l'invio della proposta.")
        }
    }


    const profileImg = (() => {
        const pic = userProfile?.profilePicture?.trim()
        if (!pic) return "/default-avatar.svg"
        return pic.startsWith("http")
            ? pic
            : `/${pic.replace(/^\/+/, "")}`
    })()

    return (
        <div className="fixed inset-0 backdrop-blur-sm bg-white/30 flex justify-center items-center z-50">
            <div className="relative bg-gray-100 rounded-3xl max-w-4xl w-full overflow-hidden">
                {/* Pulsante chiusura */}
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-pink-500 hover:text-pink-700 z-10"
                >
                    <X size={32} />
                </button>

                {/* Immagine animale */}
                <div className="relative w-full h-72 sm:h-80 md:h-96 overflow-hidden">
                    <Image
                        src="/default-pet.jpg"
                        alt="Immagine animale"
                        fill
                        className="object-cover"
                        unoptimized
                    />
                </div>

                {/* Contenuto */}
                <div className="p-6 sm:p-8 bg-gray-100">
                    {/* Titolo */}
                    <h1 className="text-5xl font-extrabold text-white -mt-24 ml-4 drop-shadow-lg">{post.name}</h1>

                    {/* Descrizione */}
                    <h2 className="text-xl font-semibold text-gray-500 mt-4">Descrizione</h2>
                    <p className="text-gray-800 mt-1">
                        {post.description || "Nessuna descrizione disponibile."}
                    </p>

                    {/* Info principali */}
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-gray-800 mt-6 text-sm sm:text-base">
                        <p><strong>Razza:</strong> {post.breed}</p>
                        <p><strong>Località:</strong> {post.location}</p>
                        <p><strong>Colore:</strong> {post.color}</p>
                        <p><strong>Sesso:</strong> {post.gender === "M" ? "Maschio" : "Femmina"}</p>
                        <p><strong>Età:</strong> {post.age} mesi</p>
                        <p><strong>Vaccinato:</strong> Sì</p>
                    </div>

                    {/* Proprietario + bottone */}
                    <div className="flex items-center justify-between mt-8">
                        <div className="flex items-center gap-3">
                            <Image
                                src={profileImg}
                                alt="Foto profilo"
                                width={48}
                                height={48}
                                className="rounded-full object-cover"
                                unoptimized
                            />
                            <span className="text-lg font-bold text-gray-800">
                                {userProfile
                                    ? `${userProfile.name} ${userProfile.surname}`
                                    : `Proprietario #${post.ownerId}`}
                            </span>
                        </div>
                        <button
                            className="bg-red-600 hover:bg-red-700 text-white font-semibold px-6 py-2 rounded-full transition"
                            onClick={handleSendProposal}
                        >
                            Manda Proposta
                        </button>

                    </div>
                </div>
            </div>
        </div>
    )
}
