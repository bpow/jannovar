package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SVAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.SVCopyNumberVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Build annotation for {@link SVCopyNumberVariant}.
 * <p>
 * Variant effects used by this builder are:
 *
 * <ul>
 * <li>{@link VariantEffect#COPY_NUMBER_CHANGE}</li>
 * <li>{@link VariantEffect#STRUCTURAL_VARIANT}</li>
 * <li>{@link VariantEffect#UPSTREAM_GENE_VARIANT}</li>
 * <li>{@link VariantEffect#DOWNSTREAM_GENE_VARIANT}</li>
 * <li>{@link VariantEffect#INTERGENIC_VARIANT}</li>
 * <li>{@link VariantEffect#CODING_TRANSCRIPT_VARIANT}</li>
 * <li>{@link VariantEffect#NON_CODING_TRANSCRIPT_VARIANT}</li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
final public class SVCopyNumberVariantAnnotationBuilder extends SVAnnotationBuilder {

	/**
	 * The SVCopyNumberVariant to be annotated.
	 */
	private final SVCopyNumberVariant svCNV;

	/**
	 * Construct the builder.
	 *
	 * @param transcript The transcript to annotate for.
	 * @param svCNV      The {@link SVCopyNumberVariant} to annotate for.
	 */
	public SVCopyNumberVariantAnnotationBuilder(TranscriptModel transcript, SVCopyNumberVariant svCNV) {
		super(transcript, svCNV);
		this.svCNV = svCNV;
	}

	@Override
	public SVAnnotation build() {
		final GenomeInterval changeInterval = svCNV.getGenomeInterval();

		// Go over the different cases from most to least pathogenic step and return most pathogenic one.

		if (changeInterval.overlapsWith(transcript.getTXRegion())) {
			return new SVAnnotation(svCNV, transcript, buildEffectSet(Sets.immutableEnumSet(
				VariantEffect.COPY_NUMBER_CHANGE, VariantEffect.STRUCTURAL_VARIANT
			)));
		} else if (so.overlapsWithUpstreamRegion(changeInterval)) {
			return new SVAnnotation(svCNV, transcript, buildEffectSet(Sets.immutableEnumSet(
				VariantEffect.STRUCTURAL_VARIANT, VariantEffect.UPSTREAM_GENE_VARIANT
			)));
		} else if (so.overlapsWithDownstreamRegion(changeInterval)) {
			return new SVAnnotation(svCNV, transcript, buildEffectSet(Sets.immutableEnumSet(
				VariantEffect.STRUCTURAL_VARIANT, VariantEffect.DOWNSTREAM_GENE_VARIANT
			)));
		} else {
			return new SVAnnotation(svCNV, transcript, buildEffectSet(Sets.immutableEnumSet(
				VariantEffect.STRUCTURAL_VARIANT, VariantEffect.INTERGENIC_VARIANT
			)));
		}
	}

	/**
	 * Return augmented {@link effects} based on the transcript alone (coding/non-coding transcript).
	 */
	private ImmutableSet<VariantEffect> buildEffectSet(Collection<VariantEffect> effects) {
		final EnumSet<VariantEffect> tmpEffects;
		if (transcript.isCoding()) {
			tmpEffects = EnumSet.of(VariantEffect.CODING_TRANSCRIPT_VARIANT);
		} else {
			tmpEffects = EnumSet.of(VariantEffect.NON_CODING_TRANSCRIPT_VARIANT);
		}
		tmpEffects.addAll(effects);
		return Sets.immutableEnumSet(tmpEffects);
	}

}
