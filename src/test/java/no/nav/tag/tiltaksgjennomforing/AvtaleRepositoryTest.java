package no.nav.tag.tiltaksgjennomforing;

import no.nav.tag.tiltaksgjennomforing.domene.Avtale;
import no.nav.tag.tiltaksgjennomforing.domene.EndreAvtale;
import no.nav.tag.tiltaksgjennomforing.domene.Maal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AvtaleRepositoryTest {

    @Autowired
    private AvtaleRepository avtaleRepository;

    @Test
    public void nyAvtaleSkalKunneLagreOgReturneresAvRepository() {
        Avtale lagretAvtale = avtaleRepository.save(TestData.minimalAvtale());

        Optional<Avtale> avtaleOptional = avtaleRepository.findById(lagretAvtale.getId());
        assertThat(avtaleOptional).isPresent();
    }

    @Test
    public void skalKunneLagreMaalFlereGanger() {
        // Lage avtale
        Avtale lagretAvtale = avtaleRepository.save(TestData.minimalAvtale());

        // Lagre maal skal fungere
        EndreAvtale endreAvtale = new EndreAvtale();
        Maal maal = new Maal();
        maal.setKategori("Kategori");
        maal.setBeskrivelse("Beskrivelse");
        endreAvtale.setMaal(List.of(maal));
        lagretAvtale.endreAvtale(1, endreAvtale);
        avtaleRepository.save(lagretAvtale);

        // Lage ny avtale
        Avtale lagretAvtale2 = avtaleRepository.save(TestData.minimalAvtale());

        // Lagre maal skal enda fungere
        EndreAvtale endreAvtale2 = new EndreAvtale();
        Maal maal2 = new Maal();
        maal2.setKategori("Kategori");
        maal2.setBeskrivelse("Beskrivelse");
        endreAvtale2.setMaal(List.of(maal2));
        lagretAvtale2.endreAvtale(1, endreAvtale2);
        avtaleRepository.save(lagretAvtale2);
    }
}
