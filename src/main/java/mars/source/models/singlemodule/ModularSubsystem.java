package mars.source.models.singlemodule;

import java.util.function.Supplier;

import com.stzteam.forgemini.io.IOSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.requests.Request;

/**
 * ModularSubsystem: El corazón de AlloyCore.
 * Gestiona el ciclo de vida: Hardware -> Snapshot -> Seguridad -> Intención -> Telemetría.
 * * @param <D> Tipo de datos (Data) del subsistema.
 * @param <A> Tipo de actor/hardware (IO) del subsistema.
 */
public abstract class ModularSubsystem<D extends Data<D>, A extends IO<D>> extends IOSubsystem {

    protected final D inputs;
    protected final A actor;
    private Request<D, A> currentRequest;
    private Telemetry<D> telemetry;
    private ActionStatus lastStatus;

    /**
     * Constructor protegido para obligar el uso del SubsystemBuilder.
     */
    protected ModularSubsystem(SubsystemBuilder<D, A> builder) {
        super(builder.getKey());
        this.inputs = builder.getInputs();
        this.actor = builder.getActor();
        this.currentRequest = builder.getInitialRequest();
        this.telemetry = builder.getTelemetry();
        this.lastStatus = ActionStatus.ok();
    }

    /**
     * Ciclo de vida sagrado de AlloyCore.
     * Este método es 'final' para que nadie rompa el orden de ejecución.
     */
    @Override
    public final void periodicLogic(){

        // --- 2. ADQUISICIÓN DE DATOS ---
        // Se lee el hardware real (Kraken, SparkMax, Sensores)
        actor.updateInputs(inputs);

        // --- 3. DETERMINISMO (SNAPSHOT) ---
        // Se congela la realidad para este ciclo
        D data = inputs.snapshot();

        // --- 4. SEGURIDAD (ABSOLUTE PERIODIC) ---
        // La "Ley de la Casa" que se ejecuta antes de cualquier intención
        absolutePeriodic(data);

        // --- 5. INTENCIÓN (REQUEST) ---
        // Se ejecuta la acción activa (Idle, Position, etc.)
        if (currentRequest != null) {
            this.lastStatus = currentRequest.apply(data, actor);
        }

        // --- 6. TELEMETRÍA ---
        // Se envían los datos finales (incluyendo el status) a la red
        if (telemetry != null) {
            telemetry.telemeterize(data, lastStatus);
        }

    }

    public abstract D getState();

    /**
     * Lógica que se ejecuta siempre, independientemente de la Request activa.
     * Ideal para Soft Limits, reseteo de sensores y estados de emergencia.
     */
    public abstract void absolutePeriodic(D inputs);

    /**
     * Registra un nuevo módulo de telemetría.
     */
    public void registerTelemetry(Telemetry<D> telemetry) {
        this.telemetry = telemetry;
    }

    /**
     * Cambia la intención (Request) actual del subsistema.
     */
    public void setRequest(Request<D, A> newRequest) {
        if (newRequest != null) {
            this.currentRequest = newRequest;
        }
    }

    public Command runRequest(Supplier<? extends Request<D, A>> requestSupplier) {
        return this.run(() -> this.setRequest(requestSupplier.get()));
    }

    public Command runRequestUntilDone(Supplier<? extends Request<D, A>> requestSupplier) {
        return this.run(() -> this.setRequest(requestSupplier.get()))
                   .until(() -> {
                       ActionStatus status = this.getLastStatus();
                       return status != null && status.isDone();
                   });
    }

    protected void overrideStatus(ActionStatus emergencyStatus) {
        this.lastStatus = emergencyStatus;
    }

    /**
     * Devuelve el último estado reportado por la Request.
     */
    public ActionStatus getLastStatus() {
        return lastStatus;
    }
}