package TorneosInternos;

import java.util.List;

public abstract class InscripcionTorneo {

	private boolean preferencial;
	private double montoPagado;

	public InscripcionTorneo() {
		this.preferencial = false;
		this.montoPagado = 0;
	}
		
		public abstract String getNombre();
		public abstract List<JugadorTorneo> getJugadores();
		public boolean incluye(JugadorTorneo jugador) {
			for (JugadorTorneo j : getJugadores()) {
				if (j.getLogin().equals(jugador.getLogin())) {
					return true;
				}
			}
			return false;
		}

		public boolean esPreferencial() {
			return preferencial;
		}

		public double getMontoPagado() {
			return montoPagado;
		}

		public void setPreferencial(boolean preferencial) {
			this.preferencial = preferencial;
		}

		public void setMontoPagado(double montoPagado) {
			this.montoPagado = montoPagado;
		}

		@Override
		public String toString() {
			return getNombre();
		
	}
	
}
