package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Map<Integer, Match> idMap;
	private Graph<Match, DefaultWeightedEdge> grafo;
	private List<Match> percorsoMigliore;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<Integer, Match>();
		this.dao.listAllMatches(idMap);
	}
	
	public void creaGrafo(int mese, int minuti) {
		this.grafo = new SimpleWeightedGraph<Match, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(idMap, mese));
		for(Arco a: this.dao.getArchi(idMap, minuti)) {
			if(this.grafo.containsVertex(a.getM1()) && this.grafo.containsVertex(a.getM2())) {
				Graphs.addEdge(this.grafo, a.getM1(), a.getM2(), a.getPeso());
			}
		}
	}
	
	public int getNumeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public String getCoppieMigliori(){
		double max = 0;
		String s = "";
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > max) {
				max = this.grafo.getEdgeWeight(e);
			}
		}
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) == max) {
				s += this.grafo.getEdgeSource(e)+" "+this.grafo.getEdgeTarget(e)+" "+this.grafo.getEdgeWeight(e)+"\n";
			}
		}
		return s;
	}
	
	public List<Match> getVertici(){
		List<Match> result = new ArrayList<Match>(this.grafo.vertexSet());
		return result;
	}
	
	public String getPercorsoMigliore(Match partenza, Match destinazione) {
		String s = "";
		this.percorsoMigliore = new ArrayList<Match>();
		List<Match> parziale = new ArrayList<Match>();
		parziale.add(partenza);
		trova(parziale, destinazione);
		for(Match m: this.percorsoMigliore) {
			s += m.toString()+"\n";
		}
		return s;
	}

	private void trova(List<Match> parziale, Match destinazione) {
		this.calcolaPeso(parziale);
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(this.calcolaPeso(parziale) > this.calcolaPeso(percorsoMigliore)) {
				this.percorsoMigliore = new ArrayList<Match>(parziale);
			}
			return;
		}
		
		for(Match m: Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(m)) {
				parziale.add(m);
				trova(parziale, destinazione);
				parziale.remove(parziale.size()-1);
			}
		}
	}
	
	private double calcolaPeso(List<Match> parziale) {
		double totale = 0;
		for(int i=0; i<parziale.size()-1;i++) {
			DefaultWeightedEdge e = this.grafo.getEdge(parziale.get(i), parziale.get(i+1));
			if(e == null) {
				e = this.grafo.getEdge(parziale.get(i+1), parziale.get(i));
			}
			totale += this.grafo.getEdgeWeight(e);
		}
		return totale;
	}
	
 
	
	
}
