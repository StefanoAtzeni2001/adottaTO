"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
    Sheet,
    SheetClose,
    SheetContent,
    SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle,
    SheetTrigger
} from "@/components/ui/sheet"

interface Props {
    profile: { name: string; surname: string }
    onUpdateAction: (name: string, surname: string) => Promise<void>
}

export default function EditProfile({ profile, onUpdateAction }: Props) {
    const [edited, setEdited] = useState(profile)

    return (
        <Sheet onOpenChange={() => setEdited(profile)}>
            <SheetTrigger asChild>
                <Button variant="outline">Modifica profilo</Button>
            </SheetTrigger>
            <SheetContent>
                <SheetHeader>
                    <SheetTitle>Modifica il tuo Profilo</SheetTitle>
                    <SheetDescription>
                        Modifica il tuo nome o cognome. Clicca su Salva le modifiche per confermare.
                    </SheetDescription>
                </SheetHeader>
                <div className="grid gap-6 px-4 py-4">
                    <div>
                        <Label>Nome</Label>
                        <Input
                            value={edited.name}
                            onChange={(e) => setEdited({ ...edited, name: e.target.value })}
                        />
                    </div>
                    <div>
                        <Label>Cognome</Label>
                        <Input
                            value={edited.surname}
                            onChange={(e) => setEdited({ ...edited, surname: e.target.value })}
                        />
                    </div>
                </div>
                <SheetFooter>
                    <Button onClick={() => onUpdateAction(edited.name, edited.surname)}>Salva le modifiche</Button>
                    <SheetClose asChild>
                        <Button variant="outline">Chiudi</Button>
                    </SheetClose>
                </SheetFooter>
            </SheetContent>
        </Sheet>
    )
}
