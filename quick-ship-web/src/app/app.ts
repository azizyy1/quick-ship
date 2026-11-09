import { Component, OnInit, signal } from '@angular/core';

interface Tournee {
  id: number;
  nom: string;
  villeDepart: string;
  villeArrivee: string;
  capaciteColis: number;
}

interface Expedition {
  id: number;
  customerId: number;
  routeId: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  readonly tournees = signal<Tournee[]>([]);
  readonly chargement = signal(true);
  readonly erreur = signal('');
  readonly enregistrement = signal(false);
  readonly messageFormulaire = signal('');

  readonly expeditions = signal<Expedition[]>([]);
  readonly chargementExpeditions = signal(true);
  readonly erreurExpeditions = signal('');
  readonly expeditionEnCours = signal(false);
  readonly messageExpedition = signal('');

  libelleTournee(routeId: number): string {
    const tournee = this.tournees().find((item) => item.id === routeId);

    return tournee
      ? `${tournee.villeDepart} → ${tournee.villeArrivee}`
      : `Tournée #${routeId}`;
  }

  ngOnInit(): void {
    void this.chargerTournees();
    void this.chargerExpeditions();
  }

  async chargerTournees(): Promise<void> {
    this.chargement.set(true);
    this.erreur.set('');

    try {
      const reponse = await fetch('/api/routes');

      if (!reponse.ok) {
        throw new Error(`Chargement des tournées refusé (${reponse.status}).`);
      }

      this.tournees.set(await reponse.json());
    } catch (erreur) {
      this.erreur.set(
        erreur instanceof Error ? erreur.message : 'Impossible de charger les tournées.',
      );
    } finally {
      this.chargement.set(false);
    }
  }

  async creerTournee(event: SubmitEvent): Promise<void> {
    event.preventDefault();
    if (this.enregistrement()) return;

    const formulaire = event.currentTarget as HTMLFormElement;
    const champs = new FormData(formulaire);

    this.enregistrement.set(true);
    this.messageFormulaire.set('');

    try {
      const reponse = await fetch('/api/routes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nom: String(champs.get('nom')).trim(),
          villeDepart: String(champs.get('villeDepart')).trim(),
          villeArrivee: String(champs.get('villeArrivee')).trim(),
          dateDepart: champs.get('dateDepart'),
          dureeEstimee: Number(champs.get('dureeEstimee')),
          capaciteColis: Number(champs.get('capaciteColis')),
        }),
      });

      if (!reponse.ok) {
        this.messageFormulaire.set(
          reponse.status === 429
            ? 'Trop de requêtes. Réessaie dans quelques secondes.'
            : `Création de la tournée refusée (${reponse.status}).`,
        );
        return;
      }

      formulaire.reset();
      this.messageFormulaire.set('Tournée créée.');
      await this.chargerTournees();
    } catch {
      this.messageFormulaire.set('Impossible de joindre le Gateway.');
    } finally {
      this.enregistrement.set(false);
    }
  }

  async chargerExpeditions(): Promise<void> {
    this.chargementExpeditions.set(true);
    this.erreurExpeditions.set('');

    try {
      const reponse = await fetch('/api/shipments');

      if (!reponse.ok) {
        throw new Error(`Chargement des expéditions refusé (${reponse.status}).`);
      }

      this.expeditions.set(await reponse.json());
    } catch (erreur) {
      this.erreurExpeditions.set(
        erreur instanceof Error ? erreur.message : 'Impossible de charger les expéditions.',
      );
    } finally {
      this.chargementExpeditions.set(false);
    }
  }

  async creerExpedition(event: SubmitEvent): Promise<void> {
    event.preventDefault();
    if (this.expeditionEnCours()) return;

    const formulaire = event.currentTarget as HTMLFormElement;
    const champs = new FormData(formulaire);

    this.expeditionEnCours.set(true);
    this.messageExpedition.set('');

    try {
      const reponse = await fetch('/api/shipments', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          customerId: Number(champs.get('customerId')),
          routeId: Number(champs.get('routeId')),
        }),
      });

      if (!reponse.ok) {
        this.messageExpedition.set(
          `Création de l’expédition refusée (${reponse.status}).`,
        );
        return;
      }

      formulaire.reset();
      this.messageExpedition.set('Expédition créée.');
      await this.chargerExpeditions();
    } catch {
      this.messageExpedition.set('Impossible de joindre le service Expéditions.');
    } finally {
      this.expeditionEnCours.set(false);
    }
  }
}
