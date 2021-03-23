package no.nav.tag.tiltaksgjennomforing.journalfoering;

import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggingService;
import no.nav.tag.tiltaksgjennomforing.avtale.*;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InternalAvtaleControllerTest {

    private static final UUID AVTALE_ID_1 = UUID.randomUUID();
    private static final UUID AVTALE_ID_2 = UUID.randomUUID();
    private static final UUID AVTALE_ID_3 = UUID.randomUUID();
    private List<AvtaleInnhold> avtaleInnholdList = avtalerMedFemGodkjenteVersjoner().stream().flatMap(avtale -> avtale.getVersjoner().stream()).collect(Collectors.toList());

    @InjectMocks
    private InternalAvtaleController internalAvtaleController;

    @Mock
    private InnloggingService innloggingService;

    @Mock
    private AvtaleRepository avtaleRepository;

    @Mock
    private AvtaleInnholdRepository avtaleInnholdRepository;

    @Test
    public void henterAvtalerTilJournalfoering() {
        doNothing().when(innloggingService).validerSystembruker();
        when(avtaleInnholdRepository.finnAvtaleVersjonerTilJournalfoering()).thenReturn(avtaleInnholdList);
        when(avtaleInnholdRepository.saveAll(anyIterable())).thenReturn(avtaleInnholdList);
        List<AvtaleTilJournalfoering> avtalerTilJournalfoering = internalAvtaleController.hentIkkeJournalfoerteAvtaler();
        verify(avtaleInnholdRepository, times(1)).saveAll(eq(avtaleInnholdList));
        assertEquals(5, avtalerTilJournalfoering.size());
        avtalerTilJournalfoering.forEach(avtaleTilJournalfoering -> assertNotNull(avtaleTilJournalfoering.getAvtaleId()));
    }

    @Test(expected = TilgangskontrollException.class)
    public void henterIkkeAvtalerTilJournalfoering() {
        doThrow(TilgangskontrollException.class).when(innloggingService).validerSystembruker();

        internalAvtaleController.hentIkkeJournalfoerteAvtaler();
        verify(avtaleRepository, never()).findAll();
    }

    @Test
    public void journalfoererAvtaler() {
        List<AvtaleInnhold> godkjenteAvtaleVersjoner = avtalerMedFemGodkjenteVersjoner().stream().flatMap(avtale ->avtale.getVersjoner().stream()).collect(Collectors.toList());
        Map<UUID, String> map = new HashMap<>();
        godkjenteAvtaleVersjoner.forEach(avtaleInnhold -> map.put(avtaleInnhold.getId(), "1"));

        doNothing().when(innloggingService).validerSystembruker();
        when(avtaleInnholdRepository.findAllById(map.keySet())).thenReturn(godkjenteAvtaleVersjoner);
        internalAvtaleController.journalfoerAvtaler(map);
        verify(avtaleInnholdRepository, atLeastOnce()).saveAll(anyIterable());
    }

    @Test(expected = TilgangskontrollException.class)
    public void journalfoererIkkeAvtaler() {
        doThrow(TilgangskontrollException.class).when(innloggingService).validerSystembruker();
        internalAvtaleController.hentIkkeJournalfoerteAvtaler();
        verify(avtaleRepository, never()).findAllById(anyIterable());
        verify(avtaleRepository, never()).saveAll(anyIterable());
    }

    private static List<Avtale> avtalerMedFemGodkjenteVersjoner() {
        Avtale avtale = TestData.enAvtaleMedAltUtfyltGodkjentAvVeileder();
        avtale.setId(AVTALE_ID_1);
        Avtale avtale2 = TestData.enAvtaleMedFlereVersjoner();
        avtale2.getVersjoner().get(0).setGodkjentAvVeileder(LocalDateTime.now());
        avtale2.setId(AVTALE_ID_2);
        Avtale avtale3 = TestData.enAvtaleMedFlereVersjoner();
        avtale3.getVersjoner().get(0).setGodkjentAvVeileder(LocalDateTime.now());
        avtale3.setId(AVTALE_ID_3);
        return List.of(avtale, avtale2, avtale3);
    }
}

