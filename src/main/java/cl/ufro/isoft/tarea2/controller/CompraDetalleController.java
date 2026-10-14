package cl.ufro.isoft.tarea2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ufro.isoft.tarea2.dto.CompraDetalleResponse;
import cl.ufro.isoft.tarea2.service.CompraDetalleService;

@RestController
@RequestMapping("/compras/detalle")
public class CompraDetalleController {

	private final CompraDetalleService compraDetalleService;

	public CompraDetalleController(CompraDetalleService compraDetalleService) {
		this.compraDetalleService = compraDetalleService;
	}

	/**
	 * GET /compras/detalle/{id}
	 */
	
	@GetMapping("/{id}")
	public CompraDetalleResponse detalle(@PathVariable long id) {
		return compraDetalleService.detalle(id);
	}

}
