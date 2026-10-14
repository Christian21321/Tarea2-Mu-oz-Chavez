package cl.ufro.isoft.tarea2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ufro.isoft.tarea2.dto.ComprasPorUnidadResponse;
import cl.ufro.isoft.tarea2.service.ComprasUnidadService;

@RestController
@RequestMapping("/compras")
public class ComprasUnidadController {

	private final ComprasUnidadService comprasUnidadService;

	public ComprasUnidadController(ComprasUnidadService comprasUnidadService) {
		this.comprasUnidadService = comprasUnidadService;
	}

	/** GET /compras/uf o /compras/dolar */
	@GetMapping("/{unidad}")
	public ComprasPorUnidadResponse comprasPorUnidad(@PathVariable String unidad) {
		return comprasUnidadService.comprasPorUnidad(unidad);
	}

}
