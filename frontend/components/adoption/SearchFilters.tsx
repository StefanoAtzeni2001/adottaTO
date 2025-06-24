import { useEffect, useState } from "react"
import Select from "react-select"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { province, dogBreeds, catBreeds, colors, genderOptions } from "@/data/constants"

interface Option {
    label: string
    value: string
}

interface Filters {
    species?: string[]
    breed?: string[]
    gender?: string
    color?: string[]
    location?: string[]
    minAge?: string
    maxAge?: string
}

interface SearchFiltersProps {
    onSearchAction: (filters: Filters) => void
}

export default function SearchFilters({ onSearchAction }: SearchFiltersProps) {
    const [species, setSpecies] = useState<Option[]>([])
    const [breed, setBreed] = useState<Option[]>([])
    const [breeds, setBreeds] = useState<Option[]>([])
    const [gender, setGender] = useState<Option | null>(null)
    const [color, setColor] = useState<Option[]>([])
    const [location, setLocation] = useState<Option[]>([])
    const [minAge, setMinAge] = useState<string>("")
    const [maxAge, setMaxAge] = useState<string>("")

    const speciesOptions: Option[] = [
        { value: "Cane", label: "Cane" },
        { value: "Gatto", label: "Gatto" }
    ]

    const genderOpts: Option[] = genderOptions.map(g => ({ value: g.value, label: g.label }))
    const colorOpts: Option[] = colors.map(c => ({ value: c, label: c }))
    const provinceOpts: Option[] = province.map(p => ({ value: p, label: p }))

    useEffect(() => {
        const selectedSpecies = species.map(s => s.value)
        if (selectedSpecies.includes("Cane") && selectedSpecies.includes("Gatto")) {
            setBreeds([...dogBreeds, ...catBreeds].map(b => ({ value: b, label: b })))
        } else if (selectedSpecies.includes("Cane")) {
            setBreeds(dogBreeds.map(b => ({ value: b, label: b })))
        } else if (selectedSpecies.includes("Gatto")) {
            setBreeds(catBreeds.map(b => ({ value: b, label: b })))
        } else {
            setBreeds([])
            setBreed([])
        }
    }, [species])

    const handleSearchClick = () => {
        onSearchAction({
            species: species.map(s => s.value),
            breed: breed.map(b => b.value),
            gender: gender ? gender.value : undefined,  // <-- stringa singola o undefined
            color: color.map(c => c.value),
            location: location.map(l => l.value),
            minAge,
            maxAge
        })
    }

    const handleClear = () => {
        setSpecies([])
        setBreed([])
        setGender(null)
        setColor([])
        setLocation([])
        setMinAge("")
        setMaxAge("")
    }

    return (
        <>
            <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4 w-full max-w-6xl">
                <Select
                    isMulti
                    placeholder="Specie"
                    value={species}
                    onChange={(val) => setSpecies(val as Option[])}
                    options={speciesOptions}
                />
                <Select
                    isMulti
                    placeholder="Razza"
                    isDisabled={breeds.length === 0}
                    value={breed}
                    onChange={(val) => setBreed(val as Option[])}
                    options={breeds}
                />
                <Select
                    placeholder="Genere"
                    value={gender}
                    onChange={(val) => setGender(val as Option | null)}
                    options={genderOpts}
                />
                <Select
                    isMulti
                    placeholder="Colore"
                    value={color}
                    onChange={(val) => setColor(val as Option[])}
                    options={colorOpts}
                />
                <Select
                    isMulti
                    placeholder="Provincia"
                    value={location}
                    onChange={(val) => setLocation(val as Option[])}
                    options={provinceOpts}
                />
                <Input
                    type="number"
                    placeholder="Età min (mesi)"
                    value={minAge}
                    onChange={(e) => setMinAge(e.target.value)}
                />
                <Input
                    type="number"
                    placeholder="Età max (mesi)"
                    value={maxAge}
                    onChange={(e) => setMaxAge(e.target.value)}
                />
            </div>

            <div className="flex gap-4 mt-4">
                <Button onClick={handleClear} variant="outline">Elimina i filtri</Button>
                <Button onClick={handleSearchClick} variant="default" className="bg-red-600 text-white">Ricerca!</Button>
            </div>
        </>
    )
}
