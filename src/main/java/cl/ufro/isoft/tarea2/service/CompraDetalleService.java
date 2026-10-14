package cl.ufro.isoft.tarea2.service;

import org.springframework.stereotype.Service;

import cl.ufro.isoft.tarea2.dto.CompraDetalleResponse;
import cl.ufro.isoft.tarea2.exception.CompraNoEncontradaException;
import cl.ufro.isoft.tarea2.model.Compra;
import cl.ufro.isoft.tarea2.repository.CompraRepository;

@Service
public class CompraDetalleService {

	private final CompraRepository compraRepository;
	private final ConversionService conversionService;

	public CompraDetalleService(CompraRepository compraRepository, ConversionService conversionService) {
		this.compraRepository = compraRepository;
		this.conversionService = conversionService;
	}

	public CompraDetalleResponse detalle(long id) {
		Compra compra = compraRepository.findById(id).orElseThrow(() -> new CompraNoEncontradaException(id));
		double valorUnidad = conversionService.valorUnidad(compra);
		return new CompraDetalleResponse(compra.id(), compra.producto(), compra.monto().doubleValue(),
				compra.unidad().getCodigoCsv(), compra.fecha(), valorUnidad,
				conversionService.montoPesos(compra, valorUnidad));
	}

}
