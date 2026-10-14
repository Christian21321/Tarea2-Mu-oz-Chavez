package cl.ufro.isoft.tarea2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.ufro.isoft.tarea2.dto.CompraUnidadDto;
import cl.ufro.isoft.tarea2.dto.ComprasPorUnidadResponse;
import cl.ufro.isoft.tarea2.model.Compra;
import cl.ufro.isoft.tarea2.model.Unidad;
import cl.ufro.isoft.tarea2.repository.CompraRepository;

@Service
public class ComprasUnidadService {

	private final CompraRepository compraRepository;
	private final ConversionService conversionService;

	public ComprasUnidadService(CompraRepository compraRepository, ConversionService conversionService) {
		this.compraRepository = compraRepository;
		this.conversionService = conversionService;
	}

	/** Compras de la unidad en el orden del CSV; el total es la suma de los montos en pesos ya redondeados. */
	public ComprasPorUnidadResponse comprasPorUnidad(String unidadUrl) {
		Unidad unidad = Unidad.desdeUrl(unidadUrl);
		List<CompraUnidadDto> compras = compraRepository.findByUnidad(unidad).stream().map(this::convertir).toList();
		long total = compras.stream().mapToLong(CompraUnidadDto::montoPesos).sum();
		return new ComprasPorUnidadResponse(unidad.getCodigoCsv(), compras, total);
	}

	private CompraUnidadDto convertir(Compra compra) {
		double valorUnidad = conversionService.valorUnidad(compra);
		return new CompraUnidadDto(compra.id(), compra.producto(), compra.monto().doubleValue(), compra.fecha(),
				valorUnidad, conversionService.montoPesos(compra, valorUnidad));
	}

}
